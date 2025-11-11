package com.ucamp.coffee.domain.notification.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.notification.service.NotificationService;
import com.ucamp.coffee.domain.notification.type.NotificationType;
import com.ucamp.coffee.domain.orders.entity.Orders;
import com.ucamp.coffee.domain.orders.event.OrderCanceledEvent;
import com.ucamp.coffee.domain.orders.event.OrderCompletedEvent;
import com.ucamp.coffee.domain.orders.event.OrderInprogressEvent;
import com.ucamp.coffee.domain.orders.event.OrderRejectedEvent;
import com.ucamp.coffee.domain.orders.event.OrderRequestEvent;
import com.ucamp.coffee.domain.orders.repository.OrdersRepository;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.subscription.repository.MemberSubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationOrderListener {

	private final NotificationService notificationService;

	private final OrdersRepository ordersRepository;
	private final MemberRepository memberRepository;
	private final MemberSubscriptionRepository memberSubscriptionRepository;
	
	private Orders findOrder(Long orderId) {
		return ordersRepository.findById(orderId)
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "존재하지 않는 주문입니다."));
	}

	/**
	 * 주문 접수 후 소비자 알림 이벤트
	 * 
	 * @param event
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Async
	public void handleOrderCompletedEvent(OrderRequestEvent event) {

		Orders order = findOrder(event.orderId());

		Store store = order.getStore();
		Member member = order.getMember();
		Member storeMember = store.getMember();

		StringBuilder msg = new StringBuilder();
		msg.append("[").append(store.getStoreName()).append("]").append(" 주문이 접수되었습니다.");

		StringBuilder msg2 = new StringBuilder();
		msg2.append("[#").append(order.getOrderNumber()).append("]").append(" 새로운 주문이 들어왔습니다.");

		notificationService.createNotification(member, NotificationType.ORDER, String.valueOf(msg), order.getOrderId());
		notificationService.createNotification(storeMember, NotificationType.ORDER, String.valueOf(msg2), order.getOrderId());

	}

	/**
	 * 사용자 주문 취소 알림 이벤트
	 * 
	 * @param event
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Async
	public void handleOrderCanceledEvent(OrderCanceledEvent event) {

		Orders order = findOrder(event.orderId());

		Member member = order.getMember();
		Store store = order.getStore();
		Member storeMember = store.getMember();

		StringBuilder msg = new StringBuilder();
		msg.append("[").append(store.getStoreName()).append("]").append(" 주문(#").append(order.getOrderNumber())
				.append(")이 정상적으로 취소되었습니다.");

		StringBuilder msg2 = new StringBuilder();
		msg2.append("[#").append(order.getOrderNumber()).append("]").append(" 주문이 고객의 요청에 의해 취소되었습니다.");

		notificationService.createNotification(member, NotificationType.ORDER, String.valueOf(msg), order.getOrderId());
		notificationService.createNotification(storeMember, NotificationType.ORDER, String.valueOf(msg2), order.getOrderId());
	}

	/**
	 * 점주 주문 접수 이벤트
	 * 
	 * @param event
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Async
	public void handleOrderStatusChangedEvent(OrderInprogressEvent event) {

		Orders order = findOrder(event.orderId());

		Member member = order.getMember();
		Store store = order.getStore();

		StringBuilder msg = new StringBuilder();
		msg.append("[").append(store.getStoreName()).append("]").append(" 고객님의 주문(#").append(order.getOrderNumber())
				.append(")이 수락되었습니다.");
		
		StringBuilder msg2 = new StringBuilder();
		msg2.append("[").append(order.getOrderNumber()).append("]").append(" 주문을 정상적으로 접수하였습니다.");

		notificationService.createNotification(member, NotificationType.ORDER, String.valueOf(msg), order.getOrderId());
		notificationService.createNotification(store.getMember(), NotificationType.ORDER, String.valueOf(msg2), order.getOrderId());

	}

	/**
	 * 점주 제조 완료 이벤트
	 * 
	 * @param event
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Async
	public void handlerOrderCompletedEvent(OrderCompletedEvent event) {

		Orders order = findOrder(event.orderId());

		Member member = order.getMember();
		Store store = order.getStore();

		StringBuilder msg = new StringBuilder();
		msg.append("[").append(store.getStoreName()).append("]").append(" 고객님의 주문(#").append(order.getOrderNumber())
				.append(")이 준비되었습니다. 매장에서 수령해주세요.");

		notificationService.createNotification(member, NotificationType.ORDER, String.valueOf(msg), order.getOrderId());

	}

	/**
	 * 주문 거부 이벤트
	 * 
	 * @param event
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Async
	public void handelrOrderRejectedEvent(OrderRejectedEvent event) {

		Orders order = findOrder(event.orderId());

		Member member = order.getMember();
		Store store = order.getStore();

		StringBuilder msg = new StringBuilder();
		msg.append("[").append(store.getStoreName()).append("]").append(" 고객님의 주문(#").append(order.getOrderNumber())
				.append(")이 매장 사정으로 취소되었습니다.");
		
		StringBuilder msg2 = new StringBuilder();
		msg2.append("[").append(order.getOrderNumber()).append("]").append(" 주문을 정상적으로 취소하였습니다.");
		

		notificationService.createNotification(member, NotificationType.ORDER, String.valueOf(msg), order.getOrderId());
		notificationService.createNotification(store.getMember(), NotificationType.ORDER, String.valueOf(msg2), order.getOrderId());
	}


}
