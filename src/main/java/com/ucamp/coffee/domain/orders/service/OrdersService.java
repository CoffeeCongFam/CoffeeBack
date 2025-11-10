package com.ucamp.coffee.domain.orders.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.orders.dto.OrderListItemDTO;
import com.ucamp.coffee.domain.orders.dto.OrderStatusRequestDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersCreateDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersCreateDTO.MenuDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersDetailResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersListResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersListSearchDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersMenuResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersStorePastRequestDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersStorePastResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersStoreResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersHistoryResponseDTO;
import com.ucamp.coffee.domain.orders.entity.OrderMenu;
import com.ucamp.coffee.domain.orders.entity.Orders;
import com.ucamp.coffee.domain.orders.event.OrderCanceledEvent;
import com.ucamp.coffee.domain.orders.event.OrderCompletedEvent;
import com.ucamp.coffee.domain.orders.event.OrderInprogressEvent;
import com.ucamp.coffee.domain.orders.event.OrderRejectedEvent;
import com.ucamp.coffee.domain.orders.event.OrderRequestEvent;
import com.ucamp.coffee.domain.orders.mapper.OrdersMapper;
import com.ucamp.coffee.domain.orders.repository.OrderMenuRepository;
import com.ucamp.coffee.domain.orders.repository.OrdersRepository;
import com.ucamp.coffee.domain.orders.type.OrderStatusType;
import com.ucamp.coffee.domain.store.dto.MenuResponseDTO;
import com.ucamp.coffee.domain.store.entity.Menu;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.repository.MenuRepository;
import com.ucamp.coffee.domain.store.repository.StoreRepository;
import com.ucamp.coffee.domain.subscription.entity.MemberSubscription;
import com.ucamp.coffee.domain.subscription.entity.SubscriptionUsageHistory;
import com.ucamp.coffee.domain.subscription.repository.MemberSubscriptionRepository;
import com.ucamp.coffee.domain.subscription.repository.SubscriptionUsageHistoryRepository;
import com.ucamp.coffee.domain.subscription.service.MemberSubscriptionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdersService {

	// 저장소 생성자 주입
	private final OrdersRepository ordersRepository;
	private final StoreRepository storeRepository;
	private final MemberRepository memberRepository;
	private final MemberSubscriptionRepository memberSubscriptionRepository;
	private final SubscriptionUsageHistoryRepository subscriptionUsageHistoryRepository;
	private final MenuRepository menuRepository;
	private final OrderMenuRepository orderMenuRepository;

	private final OrdersMapper ordersMapper;

	private final MemberSubscriptionService memberSubscriptionService;

	private final ApplicationEventPublisher publisher;

	/**
	 * 소비자 주문 생성
	 * 
	 * @param memberId
	 * @param request
	 * @return
	 */
	@Transactional
	public Long createOrder(Long memberId, OrdersCreateDTO request) {

		// 외래키 정보
		Store store = storeRepository.findById(request.getStoreId())
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "매장을 찾을 수 없습니다."));
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));
		MemberSubscription subscription = memberSubscriptionRepository.findById(request.getMemberSubscriptionId())
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "구독 정보를 찾을 수 없습니다."));

		// 보유 구독권 사용
		int quantity = request.getMenu().stream().mapToInt(OrdersCreateDTO.MenuDTO::getCount).sum();
		subscription.use(quantity);

		// 구독권 사용 내역 생성
		SubscriptionUsageHistory history = SubscriptionUsageHistory.builder().memberSubscription(subscription).build();
		subscriptionUsageHistoryRepository.save(history);

		// 주문 생성
		Orders orders = Orders.builder().store(store).member(member).memberSubscription(subscription)
				.orderType(request.getOrderType()).orderStatus(OrderStatusType.REQUEST).totalQuantity(quantity)
				.orderNumber((int) (Math.random() * 9000) + 1000).build();
		ordersRepository.save(orders);

		// 메뉴 - 주문 생성
		for (MenuDTO menu : request.getMenu()) {
			// 메뉴 찾기
			Menu menuItem = menuRepository.findById(menu.getMenuId()).orElse(null);
			OrderMenu orderMenu = OrderMenu.builder().menu(menuItem).orders(orders).quantity(menu.getCount()).build();
			orderMenuRepository.save(orderMenu);
		}

		// 주문 접수 후 소비자 및 점주 알림 생성 및 sms 전송
		publisher.publishEvent(new OrderRequestEvent(orders.getOrderId()));

		return orders.getOrderId();

	}

	/**
	 * 소비자 주문 상세 조회
	 * 
	 * @param orderId
	 * @return
	 */
	@Transactional(readOnly = true)
	public OrdersDetailResponseDTO selectOrdersById(Long orderId) {

		OrdersDetailResponseDTO response = ordersMapper.selectOrderDetailResponse(orderId);

		if (response == null) {
			throw new CommonException(ApiStatus.NOT_FOUND, "해당 주문 정보를 찾을 수 없습니다.");
		}

		return response;
	}

	/**
	 * 소비자 오늘(특정) 날짜 주문 조회
	 * 
	 * @param memberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<OrdersHistoryResponseDTO> selectTodayOrders(Long memberId) {

		if (!memberRepository.existsById(memberId)) {
			throw new CommonException(ApiStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다.");
		}

		List<OrdersHistoryResponseDTO> response = ordersMapper.selectTodayOrdersByMember(memberId);

		return response;
	}

	/**
	 * 소비자 주문취소 업데이트
	 * 
	 * @param orderId
	 */
	@Transactional
	public void updateCancelOrders(Long orderId) {

		Orders order = ordersRepository.findById(orderId)
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다"));

		// 보유 구독권 상태 복구
		int quantity = ordersMapper.countOrderMenuQuantity(orderId);
		Long memberSubscriptionId = order.getMemberSubscription().getMemberSubscriptionId();
		memberSubscriptionService.updateDailyRemainCount(memberSubscriptionId, quantity);

		// 주문 취소 알림 및 점주에게 SMS 전송
		publisher.publishEvent(new OrderCanceledEvent(orderId));

		order.cancelOrder();
	}

	/**
	 * 점주 주문 업데이트
	 * 
	 * @param orderId
	 * @param request
	 */
	@Transactional
	public void updateOrderStatus(Long orderId, OrderStatusRequestDTO request) {

		Orders order = ordersRepository.findById(orderId)
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다"));

		// 주문 접수 완료 알림 및 소비자에게 SMS 전송
		if (request.getOrderStatus() == OrderStatusType.INPROGRESS) {
			publisher.publishEvent(new OrderInprogressEvent(orderId));
		}

		// 제조 완료 알림 및 소비자에게 SMS 전송
		if (request.getOrderStatus() == OrderStatusType.COMPLETED) {
			publisher.publishEvent(new OrderCompletedEvent(orderId));
		}
		order.changeOrderStatus(request.getOrderStatus());
	}

	/**
	 * 점주 주문 거부
	 * 
	 * @param orderId
	 * @param request
	 */
	@Transactional
	public void rejectOrder(Long orderId, OrderStatusRequestDTO request) {

		Orders order = ordersRepository.findById(orderId)
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다"));

		// 보유 구독권 상태 복구
		int quantity = ordersMapper.countOrderMenuQuantity(orderId); // 주문 내역 수량 조회
		Long memberSubscriptionId = order.getMemberSubscription().getMemberSubscriptionId();
		memberSubscriptionService.updateDailyRemainCount(memberSubscriptionId, quantity); // + 주문 내역 수량 조회

		publisher.publishEvent(new OrderRejectedEvent(orderId));

		order.rejectOrder(request.getRejectedReason());
	}

	/**
	 * 점주 주문 당일 조회
	 * 
	 * @param parnterStoreId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<OrdersStoreResponseDTO> selectTodayStoreOrders(Long parnterStoreId) {

		List<OrdersStoreResponseDTO> response = ordersMapper.selectTodayStoreOrders(parnterStoreId);

		return response;
	}

	/**
	 * 점주 과거 주문 내역 전체 조회
	 * 
	 * @param request
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<OrdersStorePastResponseDTO> selectPastOrders(OrdersStorePastRequestDTO request) {

		List<OrdersStorePastResponseDTO> response = ordersMapper.selectStoreOrdersByDate(request);

		return response;
	}

	/**
	 * 사용자 과거 주문 내역 불러오기
	 * @param memberId
	 * @param period
	 * @param startDate
	 * @param endDate
	 * @param lastCreatedAt
	 * @return
	 */
	@Transactional(readOnly = true)
	public OrdersListResponseDTO selectAllOrdersHistory(Long memberId, String period, LocalDate startDate, LocalDate endDate,
			LocalDateTime lastCreatedAt) {

		LocalDateTime start;
		LocalDateTime end = LocalDateTime.now();

		if ("1M".equals(period)) {
			start = end.minusMonths(1);
		} else if ("1Y".equals(period)) {
			start = end.minusYears(1);
		} else if ("CUSTOM".equals(period)) {
			if (startDate == null || endDate == null)
				throw new IllegalArgumentException("CUSTOM 기간은 startDate, endDate가 필요합니다.");

			start = startDate.atStartOfDay();
			end = endDate.plusDays(1).atStartOfDay().minusSeconds(1);
		} else {
			start = end.minusMonths(1);
		}

		OrdersListSearchDTO search = OrdersListSearchDTO.builder().memberId(memberId).startDate(start).endDate(end)
				.lastCreatedAt(lastCreatedAt).period(period).build();


		 // 주문 목록 조회
	    List<OrderListItemDTO> ordersList = ordersMapper.selectOrdersHistoryList(search);

	    // 각 주문별 메뉴 조회 및 매핑
	    for (OrderListItemDTO order : ordersList) {
	        List<OrdersMenuResponseDTO> menus = ordersMapper.selectMenusByOrderId(order.getOrderId());
	        order.setMenuList(menus);
	    }
		String nextCursor = null;
		if (ordersList.size() > 0) {
			nextCursor = ordersList.get(ordersList.size() - 1).getCreatedAt().toString();
		}

		boolean hasNext = ordersList.size() == 10;
		OrdersListResponseDTO response = OrdersListResponseDTO.builder().ordersList(ordersList).nextCursor(nextCursor)
				.hasNext(hasNext).build();
		
		return response;
	}

}
