package com.ucamp.coffee.domain.orders.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderListItemDTO {

	private Long orderId;
	private LocalDateTime createdAt;
}
