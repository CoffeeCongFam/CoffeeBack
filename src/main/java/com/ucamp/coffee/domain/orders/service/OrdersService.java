package com.ucamp.coffee.domain.orders.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.orders.dto.OrdersCreateDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersCreateDTO.MenuDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersDetailResponseDTO;
import com.ucamp.coffee.domain.orders.entity.OrderMenu;
import com.ucamp.coffee.domain.orders.entity.Orders;
import com.ucamp.coffee.domain.orders.repository.OrderMenuRepository;
import com.ucamp.coffee.domain.orders.repository.OrdersRepository;
import com.ucamp.coffee.domain.orders.type.OrderStatusType;
import com.ucamp.coffee.domain.store.entity.Menu;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.repository.MenuRepository;
import com.ucamp.coffee.domain.store.repository.StoreRepository;
import com.ucamp.coffee.domain.subscription.entity.MemberSubscription;
import com.ucamp.coffee.domain.subscription.entity.SubscriptionUsageHistory;
import com.ucamp.coffee.domain.subscription.repository.MemberSubscriptionRepository;
import com.ucamp.coffee.domain.subscription.repository.SubscriptionUsageHistoryRepository;

import jakarta.transaction.Transactional;
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

	// 주문 생성
	@Transactional
	public Long createOrder(OrdersCreateDTO request) {

		// 외래키 정보
		Store store = storeRepository.findById(request.getStoreId())
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "매장을 찾을 수 없습니다."));
		Member member = memberRepository.findById(request.getMemberId())
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));
		MemberSubscription subscription = memberSubscriptionRepository.findById(request.getMemberSubscriptionId())
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "구독 정보를 찾을 수 없습니다."));

		// 총 주문 개수 계산
		int quantity = 0;
		for (MenuDTO menu : request.getMenu()) {
			quantity += menu.getCount();
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
				.orderType(request.getOrderType()).orderStatus(OrderStatusType.REQUEST) // 기본값 예시
				.totalQuantity(quantity).orderNumber(orderNumber).build();

		ordersRepository.save(orders);

		// 메뉴 - 주문 생성
		for (MenuDTO menu : request.getMenu()) {
			// 메뉴 찾기
			Menu menuItem = menuRepository.findById(menu.getMenuId()).orElse(null);
			OrderMenu orderMenu = OrderMenu.builder().menu(menuItem).orders(orders).quantity(menu.getCount()).build();
			orderMenuRepository.save(orderMenu);
		}

		return orders.getOrderId();

	}

	// 소비자 주문 상세 조회
	@Transactional
	public void selectOrdersById(Long orderId) {

		// 주문 데이터 가져오기
		Orders order = ordersRepository.findById(orderId)
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다."));

		// 메뉴 정보
		List<OrderMenu> orderMenuList = orderMenuRepository.findAllByOrders(order);

		// MenuDTO 리스트 생성
		List<OrdersDetailResponseDTO.MenuDTO> menuList = new ArrayList<>();

		for (OrderMenu orderMenu : orderMenuList) {
			Menu menu = orderMenu.getMenu();

			OrdersDetailResponseDTO.MenuDTO menuDTO = OrdersDetailResponseDTO.MenuDTO.builder().menuId(menu.getMenuId())
					.menuName(menu.getMenuName()).menuType(menu.getMenuType().name()).quantity(orderMenu.getQuantity())
					.build();

			menuList.add(menuDTO);
		}

		// StoreDTO 생성
		Store store = order.getStore();
		OrdersDetailResponseDTO.StoreDTO storeDTO = OrdersDetailResponseDTO.StoreDTO.builder()
				.storeName(store.getStoreName()).storeId(store.getPartnerStoreId()).build();
		
		// SubscriptionDTO 생성
//		MemberSubscription subscription = order.getMemberSubscription();
//		OrdersDetailResponseDTO.SubscriptionDTO subscriptionDTO = OrdersDetailResponseDTO.SubscriptionDTO.builder()
//				.subscriptionId(subscription.getMemberSubscriptionId()).subscriptionType(subscription.get)

		// 응담용 DTO 생성
//	    OrdersDetailResponseDTO response = OrdersDetailResponseDTO.builder().orderId(orderId).orderStatus(order.getOrderStatus().name()).

	}
}
