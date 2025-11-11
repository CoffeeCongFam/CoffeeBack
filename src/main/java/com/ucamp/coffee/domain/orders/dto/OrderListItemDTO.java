package com.ucamp.coffee.domain.orders.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderListItemDTO {

	private Long orderId;
	private LocalDateTime createdAt;
	private String orderStatus;
	private String subscriptionName;
	private String storeName;
	private String storeImg;

	// menuList는 나중에 Service에서 수동 주입
	private List<OrdersMenuResponseDTO> menuList;
}
