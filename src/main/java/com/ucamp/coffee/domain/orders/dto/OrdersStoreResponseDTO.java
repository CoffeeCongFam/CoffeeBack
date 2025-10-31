package com.ucamp.coffee.domain.orders.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 점주의 오늘 주문 내역과 그에 따른 상세 주문 내역을 응답하기 위한 DTO
 */
@Data
@NoArgsConstructor
public class OrdersStoreResponseDTO {

	private Long orderId;
	private Long memberSubscriptionId;
	
	private String orderType;
	private String orderStatus;
	private String rejectedReason;
	
	private Integer orderNumber;
	
	private LocalDateTime createdAt;
	private String tel;
	private String name;
	
	private List<MenuDTO> menuList;
	
	@Data
	@NoArgsConstructor
	public static class MenuDTO{
		private Long menuId;
		private Integer quantity;
		private String menuName;
	}

}
