package com.ucamp.coffee.domain.purchase.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.domain.purchase.dto.PurchaseAllResponseDTO;
import com.ucamp.coffee.domain.purchase.dto.PurchaseCreateDTO;
import com.ucamp.coffee.domain.purchase.service.PurchaseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PurchaseController {

	private final PurchaseService purchaseService;
	
	// 구매 생성 및 선물
	@PostMapping("api/me/purchase/new")
	public ResponseEntity<ApiResponse<?>> createPurchase(@RequestBody PurchaseCreateDTO request){
		
		Long memberId = 1L;
		Long purchaseId = purchaseService.insertPurchase(memberId, request);
		
		return ResponseMapper.successOf(Map.of("purchaseId", purchaseId));
	}
	
	// 소비자 구매 내역 전체 조회
	@GetMapping("api/me/purchase")
	public ResponseEntity<ApiResponse<?>> searchAllPurchase(){
		
		Long memberId = 1L;
		
		List<PurchaseAllResponseDTO> response = purchaseService.selectAllPurchase(memberId);
		return ResponseMapper.successOf(response);
	}
	
	// 소비자 구매 내역 상세 조회(선물 or 직접 구매)
	
	// 소비자 환불하기
}
