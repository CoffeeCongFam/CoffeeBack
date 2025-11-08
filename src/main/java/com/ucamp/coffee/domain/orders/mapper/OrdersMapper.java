package com.ucamp.coffee.domain.orders.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ucamp.coffee.domain.orders.dto.OrderListItemDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersDetailResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersListSearchDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersStorePastRequestDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersStorePastResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersStoreResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersTodayResponseDTO;

@Mapper
public interface OrdersMapper {

	// 사용자 주문 내역 상세 조회
	OrdersDetailResponseDTO selectOrderDetailResponse(Long orderId);

	// 유저의 오늘 날짜 주문 내역 조회
	List<OrdersTodayResponseDTO> selectTodayOrdersByMember(Long memberId);

	// 점주의 오늘 주문 내역 + 상세 주문 내역
	List<OrdersStoreResponseDTO> selectTodayStoreOrders(Long partnerStoreId);

	// 점주의 과거 날짜별 or 전체 주문 내역
	List<OrdersStorePastResponseDTO> selectStoreOrdersByDate(OrdersStorePastRequestDTO dto);
	
	//주문 내역 수량 계산
	int countOrderMenuQuantity(Long orderId);
	
	//사용자의 과거 주문 내역
	List<OrderListItemDTO> selectAllOrdersHistory(OrdersListSearchDTO search);

}
