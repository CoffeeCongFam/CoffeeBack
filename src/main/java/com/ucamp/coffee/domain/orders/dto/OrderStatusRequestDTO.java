package com.ucamp.coffee.domain.orders.dto;

import com.ucamp.coffee.domain.orders.type.OrderStatusType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderStatusRequestDTO {

	private OrderStatusType orderStatus;
	private String rejectedReason;
}
