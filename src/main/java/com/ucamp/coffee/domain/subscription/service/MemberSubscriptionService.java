package com.ucamp.coffee.domain.subscription.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.purchase.entity.Purchase;
import com.ucamp.coffee.domain.subscription.dto.MemberSubscriptionDTO;
import com.ucamp.coffee.domain.subscription.entity.MemberSubscription;
import com.ucamp.coffee.domain.subscription.entity.Subscription;
import com.ucamp.coffee.domain.subscription.event.NoticeBefore3Event;
import com.ucamp.coffee.domain.subscription.event.NoticeBefore7Event;
import com.ucamp.coffee.domain.subscription.event.NoticeTodayEvent;
import com.ucamp.coffee.domain.subscription.mapper.MemberSubscriptionMapper;
import com.ucamp.coffee.domain.subscription.repository.MemberSubscriptionRepository;
import com.ucamp.coffee.domain.subscription.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberSubscriptionService {

	private final MemberSubscriptionMapper memberSubscriptionMapper;
	private final MemberSubscriptionRepository memberSubscriptionRepository;
	private final SubscriptionRepository subscriptionRepository;

	private final ApplicationEventPublisher publisher;

	/**
	 * 보유 구독권 생성
	 * 
	 * @param receiver
	 * @param purchase
	 * @param subscription
	 * @param isGift
	 */
//	@Transactional => 트랜잭션 ㅔㅈ거
	public void createMemberSubscription(Purchase purchase) {

		Subscription subscription = purchase.getSubscription();
		Member receiver = purchase.getReceiver();
		Integer dailyRemainCount = subscription.getMaxDailyUsage();
		LocalDateTime subscriptionStart = LocalDateTime.now();
		LocalDateTime subscriptionEnd = subscriptionStart.plusDays(subscription.getSubscriptionPeriod());

		// 보유 구독권 entity 생성
		MemberSubscription memberSubscription = MemberSubscription.builder().member(receiver).purchase(purchase)
				.isGift(purchase.getIsGift()).dailyRemainCount(dailyRemainCount).subscriptionStart(subscriptionStart)
				.subscriptionEnd(subscriptionEnd).build();

		memberSubscriptionRepository.save(memberSubscription);

	}

	/**
	 * 주문취소/주문거부에 따른 구독권 잔여횟수 복구
	 * 
	 * @param memberSubscriptionId
	 * @param quantity
	 */
	public void updateDailyRemainCount(Long memberSubscriptionId, int quantity) {

		MemberSubscription subscription = memberSubscriptionRepository.findById(memberSubscriptionId)
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "보유 구독권이 없습니다."));

		subscription.rollbackCount(quantity);
	}

	/**
	 * 만료 7일 전에 알림
	 */
	@Transactional(readOnly = true)
	public void notificationBefore7Days() {
		List<MemberSubscriptionDTO> list = memberSubscriptionMapper.selectExpiring7Days();
		for (MemberSubscriptionDTO dto : list) {
			publisher.publishEvent(new NoticeBefore7Event(dto.getMemberId(), dto.getMemberSubscriptionId()));
		}
	}

	/**
	 * 만료 3일 전에 알림
	 */
	@Transactional(readOnly = true)
	public void notificationBefore3Days() {
		List<MemberSubscriptionDTO> list = memberSubscriptionMapper.selectExpiring3Days();
		for (MemberSubscriptionDTO dto : list) {
			publisher.publishEvent(new NoticeBefore3Event(dto.getMemberId(), dto.getMemberSubscriptionId()));
		}
	}

	/**
	 * 만료 당일 알림
	 */
	@Transactional(readOnly = true)
	public void notificationToday() {
		List<MemberSubscriptionDTO> list = memberSubscriptionMapper.selectExpiringToday();
		for (MemberSubscriptionDTO dto : list) {
			publisher.publishEvent(new NoticeTodayEvent(dto.getMemberId(), dto.getMemberSubscriptionId()));
		}
	}
}
