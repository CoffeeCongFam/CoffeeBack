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

	private Integer orderNumber;
	private String orderStatus;
	private LocalDateTime createdAt;
	private List<MenuDTO> menuList;
	private String subscriptionName;
	private String name;
	private String tel;
	
	@Data
	@NoArgsConstructor
	public static class MenuDTO{
		private String menuName;
		private Integer quantity;
	}
}
