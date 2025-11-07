package com.ucamp.coffee.domain.purchase.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.domain.purchase.dto.PortOneRequestDTO;
import com.ucamp.coffee.domain.purchase.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;
	
	@PostMapping("/api/payments/validation")
	public ResponseEntity<ApiResponse<?>> validatePayment(@RequestBody PortOneRequestDTO request){
		
		paymentService.verifyPaymentDetail(request);
		
		return ResponseMapper.successOf(null);
	
	}
}
