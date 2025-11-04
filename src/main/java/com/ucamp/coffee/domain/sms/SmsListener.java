package com.ucamp.coffee.domain.sms;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.orders.entity.Orders;
import com.ucamp.coffee.domain.orders.event.OrderCanceledEvent;
import com.ucamp.coffee.domain.orders.event.OrderCompletedEvent;
import com.ucamp.coffee.domain.orders.event.OrderInprogressEvent;
import com.ucamp.coffee.domain.orders.event.OrderRejectedEvent;
import com.ucamp.coffee.domain.orders.event.OrderRequestEvent;
import com.ucamp.coffee.domain.orders.repository.OrdersRepository;
import com.ucamp.coffee.domain.store.entity.Store;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SmsListener {

	private final SmsService smsService;
	private final OrdersRepository ordersRepository;

	/**
	 * 주문 Entity 찾기 메서드
	 * 
	 * @param orderId
	 * @return
	 */
	private Orders findOrder(Long orderId) {
		return ordersRepository.findById(orderId)
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "존재하지 않는 주문입니다."));
	}

	/**
	 * 주문 접수 후 메시지 이벤트
	 * 
	 * @param event
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Async
	public void handleOrderCompletedEvent(OrderRequestEvent event) {

		Orders order = findOrder(event.orderId());

		Member member = order.getMember();
		Store store = order.getStore();
		Member storeMember = store.getMember();

		StringBuilder msg = new StringBuilder();
		msg.append("[").append(store.getStoreName()).append("] ").append(member.getName())
				.append(" 고객님의 주문이 정상적으로 접수되었습니다.\n").append("주문번호: #").append(order.getOrderNumber());

		StringBuilder msg2 = new StringBuilder();
		msg2.append("[").append(store.getStoreName()).append("] ").append(member.getName())
				.append(" 새로운 주문이 접수되었습니다.\n").append("주문번호: #").append(order.getOrderNumber());

		smsService.sendMessage(member.getTel(), String.valueOf(msg));
		smsService.sendMessage(storeMember.getTel(), String.valueOf(msg2));
	}

	/**
	 * 소비자 주문 취소 후 점주 메시지 이벤트
	 * 
	 * @param event
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Async
	public void handleOrderCanceledEvent(OrderCanceledEvent event) {

		Orders order = findOrder(event.orderId());

		Store store = order.getStore();
		Member member = store.getMember();

		StringBuilder msg = new StringBuilder();
		msg.append("[").append(store.getStoreName()).append("] #").append(order.getOrderNumber())
				.append("번 주문이 고객의 요청에 의해 취소되었습니다.");

		smsService.sendMessage(member.getTel(), String.valueOf(msg));
	}

	/**
	 * 점주 주문 접수 후 소비자 메시지 이벤트
	 * 
	 * @param event
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Async
	public void handlerOrderInprogressEvent(OrderInprogressEvent event) {
		Orders order = findOrder(event.orderId());

		Store store = order.getStore();
		Member member = order.getMember();

		StringBuilder msg = new StringBuilder();

		msg.append("[").append(store.getStoreName()).append("] #").append(order.getOrderNumber())
				.append("번 주문이 정상적으로 수락되었습니다.");

		smsService.sendMessage(member.getTel(), String.valueOf(msg));

	}

	/**
	 * 점주 제조 완료 후 소비자 메시지 이벤트
	 * 
	 * @param event
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Async
	public void handlerOrderCompletedEvent(OrderCompletedEvent event) {

		Orders order = findOrder(event.orderId());

		Store store = order.getStore();
		Member member = order.getMember();

		StringBuilder msg = new StringBuilder();
		msg.append("[").append(store.getStoreName()).append("] #").append(order.getOrderNumber())
				.append("번 주문이 제조 완료되었습니다.");

		smsService.sendMessage(member.getTel(), String.valueOf(msg));
	}

	/**
	 * 매장 주문 거부 이벤트
	 * 
	 * @param event
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Async
	public void handlerOrderRejectedEvent(OrderRejectedEvent event) {

		Orders order = findOrder(event.orderId());

		Store store = order.getStore();
		Member member = order.getMember();

		StringBuilder msg = new StringBuilder();
		msg.append("[").append(store.getStoreName()).append("] #").append(order.getOrderNumber())
				.append("번 주문이 매장 사정으로 취소되었습니다.\n취소사유 : ").append(order.getRejectedReason());

		smsService.sendMessage(member.getTel(), String.valueOf(msg));
	}

}
