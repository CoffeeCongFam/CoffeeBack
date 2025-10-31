package com.ucamp.coffee.domain.orders.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrdersTodayResponseDTO {

	private Long orderId;
	private LocalDateTime createdAt;
	private String orderStatus;
	private String subscriptionType;
	private String subscriptionName;
	private String storeName;
}
