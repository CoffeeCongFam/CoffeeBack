package com.ucamp.coffee.domain.purchase.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.purchase.dto.PurchaseAllGiftDTO;
import com.ucamp.coffee.domain.purchase.dto.PurchaseAllResponseDTO;
import com.ucamp.coffee.domain.purchase.dto.PurchaseCreateDTO;
import com.ucamp.coffee.domain.purchase.dto.PurchaseReceiveGiftDTO;
import com.ucamp.coffee.domain.purchase.dto.PurchaseSendGiftDTO;
import com.ucamp.coffee.domain.purchase.entity.Purchase;
import com.ucamp.coffee.domain.purchase.mapper.PurchaseMapper;
import com.ucamp.coffee.domain.purchase.repository.PurchaseRepository;
import com.ucamp.coffee.domain.purchase.type.PaymentStatus;
import com.ucamp.coffee.domain.purchase.type.RefundReasonType;
import com.ucamp.coffee.domain.subscription.entity.MemberSubscription;
import com.ucamp.coffee.domain.subscription.entity.Subscription;
import com.ucamp.coffee.domain.subscription.repository.MemberSubscriptionRepository;
import com.ucamp.coffee.domain.subscription.repository.SubscriptionRepository;
import com.ucamp.coffee.domain.subscription.type.UsageStatus;

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
	 * 
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
	 * 소비자 전체 주문 내역 조회(전체, 직접구매, 선물)
	 * 
	 * @param memberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<PurchaseAllResponseDTO> selectAllPurchase(Long memberId, String type) {
		Map<String, Object> param = new HashMap<>();
		param.put("memberId", memberId);
		param.put("type", type);

		List<PurchaseAllResponseDTO> response = purchaseMapper.selectAllPurchase(param);

		for (PurchaseAllResponseDTO purchase : response) {

			// 이미 환불된 상태
			if (PaymentStatus.REFUNDED.name().equals(purchase.getPaymentStatus())) {
				purchase.getRefundReasons().add(RefundReasonType.ALREADY_REFUNDED);
			}

			// 결제 후 7일 초과
			if (LocalDateTime.now().isAfter(purchase.getPaidAt().plusDays(7))) {
				purchase.getRefundReasons().add(RefundReasonType.OVER_PERIOD);
			}

			// 이미 사용 중
			if (!UsageStatus.NOT_ACTIVATED.name().equals(purchase.getUsageStatus())) {
				purchase.getRefundReasons().add(RefundReasonType.USED_ALREADY);
			}
		}

		return response;
	}

	/**
	 * 소비자 주문 환불 처리
	 * 
	 * @param purchaseId
	 */
	@Transactional
	public void updatePurchaseRefunded(Long purchaseId) {

		Purchase purchase = purchaseRepository.findById(purchaseId)
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "구매 정보가 존재하지 않습니다."));

		// 이미 환불한 구매건이면 반려
		if (purchase.getPaymentStatus() == PaymentStatus.REFUNDED) {
			throw new CommonException(ApiStatus.CONFLICT, "이미 환불 처리된 구매 건입니다.");
		}

		// 구독권 사용한적 있으면 반려
		MemberSubscription subscription = memberSubscriptionRepository
				.findByPurchase_PurchaseId(purchase.getPurchaseId())
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "구독권 정보가 존재하지 않습니다."));

		if (subscription.getUsageStatus() != UsageStatus.NOT_ACTIVATED)
			throw new CommonException(ApiStatus.CONFLICT, "이미 사용한 구독권입니다.");

		// 사용한적 없어도 7일 지났으면 반려
		LocalDateTime purchasedAt = purchase.getCreatedAt();
		LocalDateTime today = LocalDateTime.now();

		if (today.isAfter(purchasedAt.plusDays(7))) {
			throw new CommonException(ApiStatus.CONFLICT, "구매 후 7일이 지난 구독권은 환불할 수 없습니다.");
		}

		// 환불 처리
		purchase.refundedPurchase();
	}

	/**
	 * 소비자의 모든 선물 목록 조회
	 * 
	 * @param memberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<PurchaseAllGiftDTO> selectAllGift(Long memberId) {

		return purchaseMapper.selectAllGift(memberId);
	}

	/**
	 * 소비자의 모든 보낸 선물 조회
	 * 
	 * @param memberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<PurchaseSendGiftDTO> selectAllSendGift(Long memberId) {

		return purchaseMapper.selectAllSendGift(memberId);
	}

	/**
	 * 소비자의 특정 보낸 선물 상세 조회
	 * 
	 * @param memberSubscriptionId
	 * @return
	 */
	@Transactional(readOnly = true)
	public PurchaseSendGiftDTO selectSendGiftDetail(Long purchaseId) {

		return purchaseMapper.selectSendGiftDetail(purchaseId);
	}

	/**
	 * 소비자의 모든 받은 선물 조회
	 * 
	 * @param memberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<PurchaseReceiveGiftDTO> selectAllReceivedGift(Long memberId) {

		List<PurchaseReceiveGiftDTO> list = purchaseMapper.selectAllReceivedGift(memberId);
		for (PurchaseReceiveGiftDTO dto : list) {
			dto.setUsageHistoryList(purchaseMapper.selectUsageHistoryBySubscriptionId(dto.getMemberSubscriptionId()));
		}
		return list;
	}

	/**
	 * 소비자의 특정 받은 선물 상세 조회
	 * 
	 * @param memberSubscriptionId
	 * @return
	 */
	@Transactional(readOnly = true)
	public PurchaseReceiveGiftDTO selectReceivedGiftDetail(Long memberSubscriptionId) {

		PurchaseReceiveGiftDTO response = purchaseMapper.selectReceivedGiftDetail(memberSubscriptionId);
		response.setUsageHistoryList(purchaseMapper.selectUsageHistoryBySubscriptionId(memberSubscriptionId));
		return response;
	}

	/**
	 * 소비자의 보낸 선물 상세 조회(선물 보낸 직후)
	 * 
	 * @param purchaseId
	 * @return
	 */
	@Transactional(readOnly = true)
	public PurchaseSendGiftDTO selectDetailSendGift(Long purchaseId) {

		return purchaseMapper.selectDetailSendGift(purchaseId);
	}
}
