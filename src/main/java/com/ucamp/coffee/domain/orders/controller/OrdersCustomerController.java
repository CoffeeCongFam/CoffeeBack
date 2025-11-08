package com.ucamp.coffee.domain.orders.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
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
import com.ucamp.coffee.domain.orders.dto.OrdersCreateDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersDetailResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersListResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersHistoryResponseDTO;
import com.ucamp.coffee.domain.orders.service.OrdersService;

import lombok.RequiredArgsConstructor;

/**
 * 소비자 주문 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/orders")
public class OrdersCustomerController {

	private final OrdersService ordersService;

	/**
	 * 소비자 주문 접수(생성)
	 * 
	 * @param user
	 * @param request
	 * @return
	 */
	@PostMapping("/new")
	public ResponseEntity<ApiResponse<?>> registerOrder(@AuthenticationPrincipal MemberDetails user,
			@RequestBody OrdersCreateDTO request) {

		Long memberId = user.getMemberId();

		Long orderId = ordersService.createOrder(memberId, request);

		return ResponseMapper.successOf(Map.of("orderId", orderId));
	}

	/**
	 * 소비자 주문 상세 조회
	 * 
	 * @param orderId
	 * @return
	 */
	@GetMapping("/{orderId}")
	public ResponseEntity<ApiResponse<?>> searchOrder(@PathVariable Long orderId) {

		OrdersDetailResponseDTO response = ordersService.selectOrdersById(orderId);

		return ResponseMapper.successOf(response);
	}

	/**
	 * 소비자 오늘 날짜 주문 조회
	 * 
	 * @param member
	 * @return
	 */
	@GetMapping("/today")
	public ResponseEntity<ApiResponse<?>> searchTodayOrder(@AuthenticationPrincipal MemberDetails member) {

		Long memberId = member.getMemberId();

		List<OrdersHistoryResponseDTO> response = ordersService.selectTodayOrders(memberId);
		return ResponseMapper.successOf(response);
	}

	/**
	 * 소비자 주문 취소 업데이트
	 * 
	 * @param orderId
	 * @param request
	 * @return
	 */
	@PatchMapping("/{orderId}")
	public ResponseEntity<ApiResponse<?>> cancelOrder(@PathVariable Long orderId) {

		ordersService.updateCancelOrders(orderId);

		return ResponseMapper.successOf(null);
	}

	/**
	 * 소비자 과거 주문내역 불러오기
	 * @param member
	 * @param period
	 * @param startDate
	 * @param endDate
	 * @param nextCursor
	 * @return
	 */
	@GetMapping()
	// GET /api/customer/orders?period=1M&lastCreatedAt=2025-11-01T15:00:00
	public ResponseEntity<ApiResponse<?>> selectAllOrders(@AuthenticationPrincipal MemberDetails member,
			@RequestParam(required = false) String period, // 1M, 1Y, CUSTOM
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime nextCursor) {

		Long memberId = 32L;

		OrdersListResponseDTO response = ordersService.selectAllOrdersHistory(memberId, period, startDate, endDate,
				nextCursor);

		return ResponseMapper.successOf(response);
	}

}
