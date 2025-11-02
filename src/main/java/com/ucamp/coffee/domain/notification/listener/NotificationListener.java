package com.ucamp.coffee.domain.notification.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.notification.service.NotificationService;
import com.ucamp.coffee.domain.notification.type.NotificationType;
import com.ucamp.coffee.domain.orders.event.OrderCompletedEvent;
import com.ucamp.coffee.domain.orders.event.OrderRequestEvent;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

	private final NotificationService notificationService;

	private final StoreRepository storeRepository;

	/**
	 * 주문 완료 후 소비자 알림 이벤트
	 * 
	 * @param event
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Async
	public void handleOrderCompletedEvent(OrderCompletedEvent event) {
		log.info("🔥 [OrderCompletedEvent] memberId={}, storeId={}", event.memberId(), event.storeId());
		// 가게 찾기
		Store store = storeRepository.findById(event.storeId())
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "가게 정보를 찾을 수 없습니다."));

		StringBuilder msg = new StringBuilder();
		msg.append("[").append(store.getStoreName()).append("]").append(" 주문이 접수되었습니다.");

		notificationService.createNotification(event.memberId(), NotificationType.ORDER, String.valueOf(msg));

	}

	/**
	 * 주문 접수 후 점주 알림 이벤트
	 * @param event
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Async
	public void handleOrderRequestEvent(OrderRequestEvent event) {

		// 가게 및 가게 주인 찾기
		Store store = storeRepository.findById(event.storeId())
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "가게 정보를 찾을 수 없습니다."));
		Member member = store.getMember();
		
		StringBuilder msg = new StringBuilder();
		msg.append("[").append(store.getStoreName()).append("]").append(" 새로운 주문이 들어왔습니다.");
		
		notificationService.createNotification(member.getMemberId(), NotificationType.ORDER, String.valueOf(msg));
	}
}
