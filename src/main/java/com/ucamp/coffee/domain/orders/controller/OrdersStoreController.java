package com.ucamp.coffee.domain.orders.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.domain.orders.dto.OrderStatusRequestDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersStorePastRequestDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersStorePastResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersStoreResponseDTO;
import com.ucamp.coffee.domain.orders.service.OrdersService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores/orders")
public class OrdersStoreController {

	private final OrdersService ordersService;

	/**
	 * 점주 주문 상태 업데이트
	 * @param orderId
	 * @param request
	 * @return
	 */
	@PatchMapping("/{orderId}")
	public ResponseEntity<ApiResponse<?>> modifyOrderStatus(@PathVariable Long orderId,
			@RequestBody OrderStatusRequestDTO request) {

		ordersService.updateOrderStatus(orderId, request);

		return ResponseMapper.successOf(null);

	}

	/**
	 * 점주 주문 거부(취소)
	 * @param orderId
	 * @param request
	 * @return
	 */
	@PatchMapping("/reject/{orderId}")
	public ResponseEntity<ApiResponse<?>> rejectOrder(@PathVariable Long orderId,
			@RequestBody OrderStatusRequestDTO request) {

		ordersService.rejectOrder(orderId, request);

		return ResponseMapper.successOf(null);
	}

	/**
	 * 점주 당일 주문 조회
	 * @param partnerStoreId
	 * @return
	 */
	@GetMapping("/today/{partnerStoreId}")
	public ResponseEntity<ApiResponse<?>> searchTodayOrders(@PathVariable Long partnerStoreId) {

		List<OrdersStoreResponseDTO> response = ordersService.selectTodayStoreOrders(partnerStoreId);

		return ResponseMapper.successOf(response);
	}

	/**
	 * 점주 지난 주문 내역 전체 및 날짜별 조회
	 * @param partnerStoreId
	 * @param request
	 * @return
	 */
	@GetMapping("/past/{partnerStoreId}")
	public ResponseEntity<ApiResponse<?>> searchAllOrders(@PathVariable Long partnerStoreId,
			@ModelAttribute OrdersStorePastRequestDTO request) {

		request.setPartnerStoreId(partnerStoreId);
		List<OrdersStorePastResponseDTO> response = ordersService.selectPastOrders(request);

		return ResponseMapper.successOf(response);
	}
}
