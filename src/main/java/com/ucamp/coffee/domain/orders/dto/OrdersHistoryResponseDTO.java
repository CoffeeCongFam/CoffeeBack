package com.ucamp.coffee.domain.orders.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrdersHistoryResponseDTO {

	private Long orderId;
	private LocalDateTime createdAt;
	private String orderStatus;
	private String subscriptionType;
	private String subscriptionName;
	private String storeName;
	private String storeImg;
	private List<MenuDTO> menuList;
	
	
	@Data
	@NoArgsConstructor
	public static class MenuDTO {
		
		private String menuName;
		private int quantity;
	}
}
