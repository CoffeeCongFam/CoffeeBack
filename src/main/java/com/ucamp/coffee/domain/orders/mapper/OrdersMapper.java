package com.ucamp.coffee.domain.orders.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ucamp.coffee.domain.orders.dto.OrdersDetailResponseDTO;
import com.ucamp.coffee.domain.orders.dto.OrdersTodayResponseDTO;

@Mapper
public interface OrdersMapper {

	OrdersDetailResponseDTO selectOrderDetailResponse(Long orderId);
	List<OrdersTodayResponseDTO> selectTodayOrdersByMember(Long memberId);

}
