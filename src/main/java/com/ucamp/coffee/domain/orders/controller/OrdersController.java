package com.ucamp.coffee.domain.orders.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.domain.orders.dto.OrderStatusRequestDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersCreateDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersDetailResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersTodayResponseDTO;
import com.ucamp.coffee.domain.orders.service.OrdersService;
import com.ucamp.coffee.domain.sms.SmsService;
import lombok.RequiredArgsConstructor;
import retrofit2.http.PATCH;

@RestController
@RequiredArgsConstructor
public class OrdersController {

    private final SmsService smsService;

	private final OrdersService ordersService;

    OrdersController(SmsService smsService) {
        this.smsService = smsService;
    }
	
	//주문 접수 (인증 구현 후 수정)
	@PostMapping("api/me/orders/new")
	public ResponseEntity<ApiResponse<?>> registerOrder(@RequestBody OrdersCreateDTO request) {
		
		Long orderId = ordersService.createOrder(request);
		
		return ResponseMapper.successOf(Map.of("orderId", orderId));
	}
	
	//소비자 주문 상세 조회
	@GetMapping("api/me/orders/{orderId}")
	public ResponseEntity<ApiResponse<?>> searchOrder(@PathVariable Long orderId){
		
		OrdersDetailResponseDTO response = ordersService.selectOrdersById(orderId);
		
		return ResponseMapper.successOf(response);
	}
	
	//소비자 특정 날짜 주문 조회 - 인증 구현 후 수정
	@GetMapping("api/me/orders/today")
	public ResponseEntity<ApiResponse<?>> searchTodayOrder(){
		
		Long memberId = 23L;
		List<OrdersTodayResponseDTO> response = ordersService.selectTodayOrders(memberId);
		return ResponseMapper.successOf(response);
	}
	
	//소비자 주문취소 업데이트
	@PATCH("api/me/orders/{orderId}")
	public ResponseEntity<ApiResponse<?>> cancelOrder(@PathVariable Long orderId){
		
		ordersService.updateCancelOrders(orderId);
		
		return ResponseMapper.successOf(null);
	}
	
	//점주 주문 상태 업데이트
	@PATCH("api/store/orders/{orderId}")
	public ResponseEntity<ApiResponse<?>> modifyOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatusRequestDTO request){
		
		return ResponseMapper.successOf(null);

	}
	
	//점주 전체 주문 조회
	
//
//	주문상태변경
//
//	날짜주문조회
//
//	당일주문조회
	//점주 주문 상세 보기
}
