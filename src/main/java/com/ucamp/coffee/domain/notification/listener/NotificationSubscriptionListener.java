package com.ucamp.coffee.domain.notification.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
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
import com.ucamp.coffee.domain.subscription.entity.MemberSubscription;
import com.ucamp.coffee.domain.subscription.event.NoticeBefore3Event;
import com.ucamp.coffee.domain.subscription.event.NoticeBefore7Event;
import com.ucamp.coffee.domain.subscription.event.NoticeTodayEvent;
import com.ucamp.coffee.domain.subscription.repository.MemberSubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationSubscriptionListener {

	private final NotificationService notificationService;
	
	private final MemberRepository memberRepository;
	private final MemberSubscriptionRepository memberSubscriptionRepository;
	
	private Member findMember(Long memberId) {
		return memberRepository.findById(memberId)
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "회원 정보가 없습니다."));
	}

	private MemberSubscription findMS(Long memberSubscriptionId) {
		return memberSubscriptionRepository.findSubscriptionById(memberSubscriptionId)
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "구독 정보를 찾을 수 없습니다."));
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

		notificationService.createNotification(member, NotificationType.SUBSCRIPTION, String.valueOf(msg), ms.getMemberSubscriptionId());

	}

	/**
	 * 구독권 만료 3일전 알림
	 * @param event
	 */
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

		notificationService.createNotification(member, NotificationType.SUBSCRIPTION, String.valueOf(msg), ms.getMemberSubscriptionId());
	}
	
	/**
	 * 구독권 만료 당일 알림
	 * @param event
	 */
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
		
		notificationService.createNotification(member, NotificationType.SUBSCRIPTION, String.valueOf(msg), ms.getMemberSubscriptionId());
	}
}
