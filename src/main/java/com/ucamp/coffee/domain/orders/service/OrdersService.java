package com.ucamp.coffee.domain.orders.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.orders.dto.OrderStatusRequestDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersCreateDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersCreateDTO.MenuDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersDetailResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersMessageDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersStorePastRequestDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersStorePastResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersStoreResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersTodayResponseDTO;
import com.ucamp.coffee.domain.orders.entity.OrderMenu;
import com.ucamp.coffee.domain.orders.entity.Orders;
import com.ucamp.coffee.domain.orders.event.OrderCompletedEvent;
import com.ucamp.coffee.domain.orders.event.OrderRequestEvent;
import com.ucamp.coffee.domain.orders.mapper.OrdersMapper;
import com.ucamp.coffee.domain.orders.repository.OrderMenuRepository;
import com.ucamp.coffee.domain.orders.repository.OrdersRepository;
import com.ucamp.coffee.domain.orders.type.OrderStatusType;
import com.ucamp.coffee.domain.sms.SmsService;
import com.ucamp.coffee.domain.store.entity.Menu;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.repository.MenuRepository;
import com.ucamp.coffee.domain.store.repository.StoreRepository;
import com.ucamp.coffee.domain.subscription.entity.MemberSubscription;
import com.ucamp.coffee.domain.subscription.entity.SubscriptionUsageHistory;
import com.ucamp.coffee.domain.subscription.repository.MemberSubscriptionRepository;
import com.ucamp.coffee.domain.subscription.repository.SubscriptionUsageHistoryRepository;
import com.ucamp.coffee.domain.subscription.type.UsageStatus;

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

	private final SmsService smsService;
	
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

		// 총 주문 개수 계산
		int quantity = 0;
		for (MenuDTO menu : request.getMenu()) {
			quantity += menu.getCount();
		}

		// 만료되었거나 주문 수가 당일 잔여 수보다 적으면
		if (subscription.getUsageStatus() == UsageStatus.EXPIRED || subscription.getDailyRemainCount() < quantity) {
			throw new CommonException(ApiStatus.BAD_REQUEST, "사용할 수 없는 구독권입니다.");
		}

		// 만약 사용한적 없는 구독권이라면 상태 변경
		if (subscription.getUsageStatus() == UsageStatus.NOT_ACTIVATED) {
			subscription.activateSubscription();
		}

		// 일 잔여 횟수 차감 후 저장
		int remain = subscription.getDailyRemainCount();
		subscription.setDailyRemainCount(remain - quantity);

		// 구독권 사용 내역 생성
		SubscriptionUsageHistory history = SubscriptionUsageHistory.builder().memberSubscription(subscription).build();
		subscriptionUsageHistoryRepository.save(history);

		// 주문 번호 생성
		int orderNumber = (int) (Math.random() * 9000) + 1000;

		// 주문 생성
		Orders orders = Orders.builder().store(store).member(member).memberSubscription(subscription)
				.orderType(request.getOrderType()).orderStatus(OrderStatusType.REQUEST).totalQuantity(quantity)
				.orderNumber(orderNumber).build();

		ordersRepository.save(orders);

		// 메뉴 - 주문 생성
		for (MenuDTO menu : request.getMenu()) {
			// 메뉴 찾기
			Menu menuItem = menuRepository.findById(menu.getMenuId()).orElse(null);
			OrderMenu orderMenu = OrderMenu.builder().menu(menuItem).orders(orders).quantity(menu.getCount()).build();
			orderMenuRepository.save(orderMenu);
		}

		// 주문 메시지 객체 생성(추후 수정)
		OrdersMessageDTO message = OrdersMessageDTO.builder().name("주현석").storeName(store.getStoreName())
				.tel("01091205456").orderNumber(orders.getOrderNumber()).build();

//		smsService.sendCustomerOrderCompletedMessage(message);
		
		//주문 접수 후 소비자 알림 생성
		publisher.publishEvent(new OrderCompletedEvent(memberId, store.getPartnerStoreId()));
		
		//주문 접수 후 점주 알림 생성
		publisher.publishEvent(new OrderRequestEvent(memberId, store.getPartnerStoreId()));

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
	public List<OrdersTodayResponseDTO> selectTodayOrders(Long memberId) {

		if (!memberRepository.existsById(memberId)) {
			throw new CommonException(ApiStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다.");
		}

		List<OrdersTodayResponseDTO> response = ordersMapper.selectTodayOrdersByMember(memberId);

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
		
		//소비자 주문 취소 알림

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

}
