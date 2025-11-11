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
import com.ucamp.coffee.domain.notification.service.NotificationService;
import com.ucamp.coffee.domain.notification.type.NotificationType;
import com.ucamp.coffee.domain.purchase.entity.Purchase;
import com.ucamp.coffee.domain.purchase.event.GiftReceiveEvent;
import com.ucamp.coffee.domain.purchase.repository.PurchaseRepository;
import com.ucamp.coffee.domain.subscription.entity.Subscription;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationGiftListener {

	private final PurchaseRepository purchseRepository;
	private final NotificationService notificationService;

	/**
	 * 선물 전송 알림 이벤트
	 * 
	 * @param event
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Async
	public void handleGiftReceiveEvent(GiftReceiveEvent event) {

		Purchase purchase = purchseRepository.findById(event.purchaseId())
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "결제 정보가 존재하지 않습니다"));

		Member buyer = purchase.getBuyer();
		Member receiver = purchase.getReceiver();
		Subscription subscription = purchase.getSubscription();

		StringBuilder msg = new StringBuilder();
		msg.append("[").append(buyer.getName()).append("] 님이 [").append(subscription.getSubscriptionName())
				.append("] 을 선물했어요!");
		
		notificationService.createNotification(receiver, NotificationType.GIFT, String.valueOf(msg), event.memberSubscriptionId());
	}
}
