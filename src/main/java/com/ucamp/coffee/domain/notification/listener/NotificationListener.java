package com.ucamp.coffee.domain.notification.listener;

import org.springframework.boot.json.JsonWriter.Members;
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
import com.ucamp.coffee.domain.notification.service.SseService;
import com.ucamp.coffee.domain.notification.type.NotificationType;
import com.ucamp.coffee.domain.orders.entity.Orders;
import com.ucamp.coffee.domain.orders.event.OrderCanceledEvent;
import com.ucamp.coffee.domain.orders.event.OrderCompletedEvent;
import com.ucamp.coffee.domain.orders.event.OrderInprogressEvent;
import com.ucamp.coffee.domain.orders.event.OrderRejectedEvent;
import com.ucamp.coffee.domain.orders.event.OrderRequestEvent;
import com.ucamp.coffee.domain.orders.repository.OrdersRepository;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.subscription.entity.MemberSubscription;
import com.ucamp.coffee.domain.subscription.event.NoticeBefore3Event;
import com.ucamp.coffee.domain.subscription.event.NoticeBefore7Event;
import com.ucamp.coffee.domain.subscription.event.NoticeTodayEvent;
import com.ucamp.coffee.domain.subscription.repository.MemberSubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationListener {

	private final NotificationService notificationService;

	private final OrdersRepository ordersRepository;
	private final MemberRepository memberRepository;
	private final MemberSubscriptionRepository memberSubscriptionRepository;
	
	private Orders findOrder(Long orderId) {
		return ordersRepository.findById(orderId)
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "존재하지 않는 주문입니다."));
	}

	private Member findMember(Long memberId) {
		return memberRepository.findById(memberId)
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "회원 정보가 없습니다."));
	}

	private MemberSubscription findMS(Long memberSubscriptionId) {
		return memberSubscriptionRepository.findSubscriptionById(memberSubscriptionId)
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "구독 정보를 찾을 수 없습니다."));
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

		notificationService.createNotification(member, NotificationType.ORDER, String.valueOf(msg));
		notificationService.createNotification(storeMember, NotificationType.ORDER, String.valueOf(msg2));

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

		notificationService.createNotification(member, NotificationType.ORDER, String.valueOf(msg));
		notificationService.createNotification(storeMember, NotificationType.ORDER, String.valueOf(msg2));
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

		notificationService.createNotification(member, NotificationType.ORDER, String.valueOf(msg));

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

		notificationService.createNotification(member, NotificationType.ORDER, String.valueOf(msg));

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

		notificationService.createNotification(member, NotificationType.ORDER, String.valueOf(msg));
	}

	/**
	 * 구독권 만료 7일전 알림
	 * 
	 * @param event
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Async
	public void handleNoticeBefore7Event(NoticeBefore7Event event) {

		Member member = findMember(event.memberId());
		MemberSubscription ms = findMS(event.memberSubscriptionId());

		String subscriptionName = ms.getPurchase().getSubscription().getSubscriptionName();
		String storeName = ms.getPurchase().getSubscription().getStore().getStoreName();

		StringBuilder msg = new StringBuilder();
		msg.append("[").append(storeName).append("] '").append(subscriptionName).append("' 구독권이 일주일 후 만료됩니다.");

		notificationService.createNotification(member, NotificationType.SUBSCRIPTION, String.valueOf(msg));

	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Async
	public void handleNoticeBefore3Event(NoticeBefore3Event event) {

		Member member = findMember(event.memberId());
		MemberSubscription ms = findMS(event.memberSubscriptionId());

		String subscriptionName = ms.getPurchase().getSubscription().getSubscriptionName();
		String storeName = ms.getPurchase().getSubscription().getStore().getStoreName();

		StringBuilder msg = new StringBuilder();
		msg.append("[").append(storeName).append("] '").append(subscriptionName).append("' 구독권이 3일 후 만료됩니다.");

		notificationService.createNotification(member, NotificationType.SUBSCRIPTION, String.valueOf(msg));
	}
	
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Async
	public void handleNoticeTodayEvent(NoticeTodayEvent event) {
		
		Member member = findMember(event.memberId());
		MemberSubscription ms = findMS(event.memberSubscriptionId());
		
		String subscriptionName = ms.getPurchase().getSubscription().getSubscriptionName();
		String storeName = ms.getPurchase().getSubscription().getStore().getStoreName();
		
		StringBuilder msg = new StringBuilder();
		msg.append("[").append(storeName).append("] '").append(subscriptionName).append("' 구독권이 오늘 만료됩니다.");
		
		notificationService.createNotification(member, NotificationType.SUBSCRIPTION, String.valueOf(msg));
	}

}
