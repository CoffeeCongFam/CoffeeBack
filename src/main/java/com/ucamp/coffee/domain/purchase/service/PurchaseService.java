package com.ucamp.coffee.domain.purchase.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.purchase.dto.PurchaseAllResponseDTO;
import com.ucamp.coffee.domain.purchase.dto.PurchaseCreateDTO;
import com.ucamp.coffee.domain.purchase.entity.Purchase;
import com.ucamp.coffee.domain.purchase.mapper.PurchaseMapper;
import com.ucamp.coffee.domain.purchase.repository.PurchaseRepository;
import com.ucamp.coffee.domain.purchase.type.PaymentStatus;
import com.ucamp.coffee.domain.subscription.entity.MemberSubscription;
import com.ucamp.coffee.domain.subscription.entity.Subscription;
import com.ucamp.coffee.domain.subscription.repository.MemberSubscriptionRepository;
import com.ucamp.coffee.domain.subscription.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseService {

	private final MemberRepository memberRepository;
	private final SubscriptionRepository subscriptionRepository;
	private final PurchaseRepository purchaseRepository;
	private final MemberSubscriptionRepository memberSubscriptionRepository;
	
	private final PurchaseMapper purchaseMapper;

	/**
	 * 주문 생성 및 선물
	 * 
	 * @param memberId
	 * @param request
	 * @return
	 */
	@Transactional
	public Long insertPurchase(Long memberId, PurchaseCreateDTO request) {

		// 구매자 정보
		Member buyer = memberRepository.findById(memberId)
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "회원 정보가 존재하지 않습니다."));

		// 수신자 정보
		Member receiver = buyer;
		String isGift = "N";
		if (request.getReceiverMemberId() != null) {
			receiver = memberRepository.findById(request.getReceiverMemberId())
					.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "선물할 회원 정보가 존재하지 않습니다."));

			// 구매자랑 수신자가 다르면 선물
			isGift = "Y";
		}

		// 구독권 정보
		Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "구독권 정보가 존재하지 않습니다."));

		// Purchase Entity 생성
		Purchase purchase = Purchase.builder().buyer(buyer).receiver(receiver).subscription(subscription).isGift(isGift)
				.giftMessage(request.getGiftMessage()).purchaseType(request.getPurchaseType())
				.paymentStatus(PaymentStatus.PAID).build();

		// DB 저장
		Purchase savedPurchase = purchaseRepository.save(purchase);

		// 보유 구독권 생성
		createMemberSubscription(receiver, savedPurchase, subscription, isGift);

		// PK값 return
		return savedPurchase.getPurchaseId();
	}

	/**
	 * 보유 구독권 생성
	 * @param receiver
	 * @param purchase
	 * @param subscription
	 * @param isGift
	 */
	private void createMemberSubscription(Member receiver, Purchase purchase, Subscription subscription,
			String isGift) {

		Integer dailyRemainCount = subscription.getMaxDailyUsage();
		LocalDateTime subscriptionStart = LocalDateTime.now();
		LocalDateTime subscriptionEnd = subscriptionStart.plusDays(subscription.getSubscriptionPeriod());

		// 보유 구독권 entity 생성
		MemberSubscription memberSubscription = MemberSubscription.builder().member(receiver).purchase(purchase)
				.isGift(isGift).dailyRemainCount(dailyRemainCount).subscriptionStart(subscriptionStart)
				.subscriptionEnd(subscriptionEnd).build();

		memberSubscriptionRepository.save(memberSubscription);

	}
	
	/**
	 * 소비자 전체 주문 내역 조회
	 * @param memberId
	 * @return
	 */
	public List<PurchaseAllResponseDTO> selectAllPurchase(Long memberId) {
		
		return purchaseMapper.selectAllPurchase(memberId);
	}
}
