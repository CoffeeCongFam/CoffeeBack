package com.ucamp.coffee.domain.purchase.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.common.security.MemberDetails;
import com.ucamp.coffee.domain.purchase.dto.PortOneTempResponseDTO;
import com.ucamp.coffee.domain.purchase.dto.PurchaseAllResponseDTO;
import com.ucamp.coffee.domain.purchase.dto.PurchaseCreateDTO;
import com.ucamp.coffee.domain.purchase.service.PurchaseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/purchase")
public class PurchaseController {

	private final PurchaseService purchaseService;

	
	/**
	 * 구매 생성 및 선물
	 * @param request
	 * @param member
	 * @return
	 */
	@PostMapping("/new")
	public ResponseEntity<ApiResponse<?>> createPurchase(@RequestBody PurchaseCreateDTO request,
			@AuthenticationPrincipal MemberDetails member) {

		Long memberId = member.getMemberId();
		PortOneTempResponseDTO response = purchaseService.insertPurchase(memberId, request);

		return ResponseMapper.successOf(response);
	}

	/**
	 * 소비자 구매 내역 조회(전체, 선물, 직접구매)
	 * @param type
	 * @param member
	 * @return
	 */
	@GetMapping()
	public ResponseEntity<ApiResponse<?>> searchAllPurchase(@RequestParam(required = false) String type,
			@AuthenticationPrincipal MemberDetails member) {

		Long memberId = member.getMemberId();

		List<PurchaseAllResponseDTO> response = purchaseService.selectAllPurchase(memberId, type);
		return ResponseMapper.successOf(response);
	}
	
	/**
	 * 소비자 구매내역 단건 조회
	 * @param purchaseId
	 * @return
	 */
	@GetMapping("/{purchaseId}")
	public ResponseEntity<ApiResponse<?>> searchPurchase(@PathVariable Long purchaseId) {


		PurchaseAllResponseDTO response = purchaseService.selectPurchase(purchaseId);
		
		return ResponseMapper.successOf(response);
	}

	/**
	 * 소비자 환불하기
	 * @param purchaseId
	 * @return
	 */
	@PatchMapping("refund/{purchaseId}")
	public ResponseEntity<ApiResponse<?>> modifyPurchaseRefunded(@PathVariable Long purchaseId) {

		LocalDateTime refundedAt = purchaseService.updatePurchaseRefunded(purchaseId);

		return ResponseMapper.successOf(Map.of("refundedAt", refundedAt));
	}

	
}
