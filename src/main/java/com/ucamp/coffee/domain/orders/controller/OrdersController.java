package com.ucamp.coffee.domain.orders.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.domain.orders.dto.OrderStatusRequestDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersCreateDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersDetailResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersStorePastRequestDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersStorePastResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersStoreResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersTodayResponseDTO;
import com.ucamp.coffee.domain.orders.service.OrdersService;
import com.ucamp.coffee.domain.subscription.service.MemberSubscriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrdersController {

	private final OrdersService ordersService;
	
	private final MemberSubscriptionService mss;

	// 주문 접수 (인증 구현 후 수정)
	@PostMapping("api/me/orders/new")
	public ResponseEntity<ApiResponse<?>> registerOrder(@RequestBody OrdersCreateDTO request) {

		Long memberId = 1L; //-------------------로그인 구현 후 수정 예정
		
		Long orderId = ordersService.createOrder(memberId, request);

		return ResponseMapper.successOf(Map.of("orderId", orderId));
	}

	// 소비자 주문 상세 조회
	@GetMapping("api/me/orders/{orderId}")
	public ResponseEntity<ApiResponse<?>> searchOrder(@PathVariable Long orderId) {

		OrdersDetailResponseDTO response = ordersService.selectOrdersById(orderId);

		return ResponseMapper.successOf(response);
	}

	// 소비자 오늘(특정) 날짜 주문 조회 - 인증 구현 후 수정
	@GetMapping("api/me/orders/today")
	public ResponseEntity<ApiResponse<?>> searchTodayOrder() {

		Long memberId = 1L; //-------------------로그인 구현 후 수정 예정
		
		List<OrdersTodayResponseDTO> response = ordersService.selectTodayOrders(memberId);
		return ResponseMapper.successOf(response);
	}

	// 소비자 주문취소 업데이트
	@PatchMapping("api/me/orders/{orderId}")
	public ResponseEntity<ApiResponse<?>> cancelOrder(@PathVariable Long orderId) {

		ordersService.updateCancelOrders(orderId);

		return ResponseMapper.successOf(null);
	}

	// 점주 주문 상태 업데이트
	@PatchMapping("api/stores/orders/{orderId}")
	public ResponseEntity<ApiResponse<?>> modifyOrderStatus(@PathVariable Long orderId,
			@RequestBody OrderStatusRequestDTO request) {

		ordersService.updateOrderStatus(orderId, request);

		return ResponseMapper.successOf(null);

	}

	// 점주 주문 거부
	@PatchMapping("api/stores/orders/reject/{orderId}")
	public ResponseEntity<ApiResponse<?>> rejectOrder(@PathVariable Long orderId,
			@RequestBody OrderStatusRequestDTO request) {

		ordersService.rejectOrder(orderId, request);

		return ResponseMapper.successOf(null);
	}

	// 점주 당일 주문 조회
	@GetMapping("api/stores/orders/today/{partnerStoreId}")
	public ResponseEntity<ApiResponse<?>> searchTodayOrders(@PathVariable Long partnerStoreId) {

		List<OrdersStoreResponseDTO> response = ordersService.selectTodayStoreOrders(partnerStoreId);

		return ResponseMapper.successOf(response);
	}

	// 점주 지난 주문 내역 전체 및 날짜별 조회
	@GetMapping("api/stores/orders/past/{partnerStoreId}")
	public ResponseEntity<ApiResponse<?>> searchAllOrders(@PathVariable Long partnerStoreId,
			@ModelAttribute OrdersStorePastRequestDTO request) {

		request.setPartnerStoreId(partnerStoreId);
		List<OrdersStorePastResponseDTO> response = ordersService.selectPastOrders(request);

		return ResponseMapper.successOf(response);
	}

	@GetMapping("/test/test/test")
	public ResponseEntity<ApiResponse<?>> asdf(){
		
		mss.notificationBefore7Days();
		return ResponseMapper.successOf(null);
	}

}
