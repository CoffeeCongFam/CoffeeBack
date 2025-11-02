package com.ucamp.coffee.domain.orders.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 점주의 과거 내역 응답하기 위한 response DTO
 */
@Data
@NoArgsConstructor
public class OrdersStorePastResponseDTO {

	private Long orderId;
	private int orderNumber;
	private LocalDateTime createdAt;
	private String orderStatus;
	private Long memberSubscriptionId;
	private String subscriptionName;
	private String orderType;
	private String name;
	private String tel;
	private List<MenuDTO> menuList;
	
	@Data
	@NoArgsConstructor
	public static class MenuDTO{
		private Long menuId;
		private String menuName;
		private int quantity;
	}
}
