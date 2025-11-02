package com.ucamp.coffee.domain.purchase.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.domain.purchase.dto.PurchaseAllGiftDTO;
import com.ucamp.coffee.domain.purchase.dto.PurchaseAllResponseDTO;
import com.ucamp.coffee.domain.purchase.dto.PurchaseCreateDTO;
import com.ucamp.coffee.domain.purchase.dto.PurchaseReceiveGiftDTO;
import com.ucamp.coffee.domain.purchase.dto.PurchaseSendGiftDTO;
import com.ucamp.coffee.domain.purchase.service.PurchaseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PurchaseController {

	private final PurchaseService purchaseService;

	// 구매 생성 및 선물
	@PostMapping("/api/me/purchase/new")
	public ResponseEntity<ApiResponse<?>> createPurchase(@RequestBody PurchaseCreateDTO request) {

		Long memberId = 2L; // -----------------------------로그인 추후 수정
		Long purchaseId = purchaseService.insertPurchase(memberId, request);

		return ResponseMapper.successOf(Map.of("purchaseId", purchaseId));
	}

	// 소비자 구매 내역 조회(전체, 선물, 직접구매)
	@GetMapping("/api/me/purchase")
	public ResponseEntity<ApiResponse<?>> searchAllPurchase(@RequestParam(required = false) String type) {

		Long memberId = 1L; // -----------------------------로그인 추후 수정

		List<PurchaseAllResponseDTO> response = purchaseService.selectAllPurchase(memberId, type);
		return ResponseMapper.successOf(response);
	}

	// 소비자 환불하기
	@PatchMapping("/api/me/purchase/{purchaseId}")
	public ResponseEntity<ApiResponse<?>> modifyPurchaseRefunded(@PathVariable Long purchaseId) {

		purchaseService.updatePurchaseRefunded(purchaseId);

		return ResponseMapper.successOf(null);
	}

	// 선물 전체 목록 조회
	@GetMapping("/api/me/purchase/gift")
	public ResponseEntity<ApiResponse<?>> searchAllGift() {

		Long memberId = 1L; // -----------------------------로그인 추후 수정

		List<PurchaseAllGiftDTO> response = purchaseService.selectAllGift(memberId);

		return ResponseMapper.successOf(response);
	}

	// 보낸 선물 전체 조회
	@GetMapping("/api/me/purchase/gift/send")
	public ResponseEntity<ApiResponse<?>> searchSendGift(@RequestParam(required = false) String purchaseId) {

		Long memberId = 1L; // -----------------------------로그인 추후 수정

		Object response;

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

	// 받은 선물 전체 조회
	@GetMapping("/api/me/purchase/gift/receive")
	public ResponseEntity<ApiResponse<?>> searchReceiveGift(
			@RequestParam(required = false) String memberSubscriptionId) {

		Long memberId = 1L; // -----------------------------로그인 추후 수정

		Object response;
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

	// 소비자 선물 보낸 후, 특정 선물 상세 조회
	@GetMapping("/api/me/purchase/gift/{purchaseId}")
	public ResponseEntity<ApiResponse<?>> searchDetailGift(@PathVariable Long purchaseId) {

		PurchaseSendGiftDTO response = purchaseService.selectDetailSendGift(purchaseId);

		return ResponseMapper.successOf(response);
	}
}
