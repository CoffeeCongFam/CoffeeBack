package com.ucamp.coffee.domain.purchase.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.common.security.MemberDetails;
import com.ucamp.coffee.domain.purchase.dto.PurchaseAllGiftDTO;
import com.ucamp.coffee.domain.purchase.dto.PurchaseReceiveGiftDTO;
import com.ucamp.coffee.domain.purchase.dto.PurchaseSendGiftDTO;
import com.ucamp.coffee.domain.purchase.service.PurchaseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/purchase/gift")
public class GiftController {

	private final PurchaseService purchaseService;

	/**
	 * 선물 전체 목록 조회
	 * @param member
	 * @return
	 */
	@GetMapping()
	public ResponseEntity<ApiResponse<?>> searchAllGift(@AuthenticationPrincipal MemberDetails member) {

		Long memberId = member.getMemberId();

		List<PurchaseAllGiftDTO> response = purchaseService.selectAllGift(memberId);

		return ResponseMapper.successOf(response);
	}

	/**
	 * 보낸 선물 전체 조회
	 * @param purchaseId
	 * @param member
	 * @return
	 */
	@GetMapping("/send")
	public ResponseEntity<ApiResponse<?>> searchSendGift(@RequestParam(required = false) String purchaseId,
			@AuthenticationPrincipal MemberDetails member) {

		Long memberId = member.getMemberId();

		Object response;

		//주문ID 파라미터가 없으면 전체조회, 있으면 단일 조회
		if (purchaseId != null) {
			Long parsedId = Long.parseLong(purchaseId);
			PurchaseSendGiftDTO giftDetail = purchaseService.selectSendGiftDetail(parsedId);
			response = giftDetail;
		} else {
			List<PurchaseSendGiftDTO> giftList = purchaseService.selectAllSendGift(memberId);
			response = giftList;
		}

		return ResponseMapper.successOf(response);
	}

	/**
	 * 받은 선물 전체 조회
	 * @param memberSubscriptionId
	 * @param member
	 * @return
	 */
	@GetMapping("/receive")
	public ResponseEntity<ApiResponse<?>> searchReceiveGift(@RequestParam(required = false) String memberSubscriptionId,
			@AuthenticationPrincipal MemberDetails member) {

		Long memberId = member.getMemberId();

		Object response;
		
		//보유 구독권 ID 파라미터가 없으면 전체 조회, 있으면 단일 조회
		if (memberSubscriptionId != null) {
			Long parsedId = Long.parseLong(memberSubscriptionId);
			PurchaseReceiveGiftDTO giftDetail = purchaseService.selectReceivedGiftDetail(parsedId);
			response = giftDetail;
		} else {
			List<PurchaseReceiveGiftDTO> giftList = purchaseService.selectAllReceivedGift(memberId);
			response = giftList;
		}

		return ResponseMapper.successOf(response);

	}

	/**
	 * 소비자 선물 보낸 후, 특정 선물 상세 조회
	 * @param purchaseId
	 * @return
	 */
	@GetMapping("/{purchaseId}")
	public ResponseEntity<ApiResponse<?>> searchDetailGift(@PathVariable Long purchaseId) {

		PurchaseSendGiftDTO response = purchaseService.selectDetailSendGift(purchaseId);

		return ResponseMapper.successOf(response);
	}
}
