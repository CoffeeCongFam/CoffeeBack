package com.ucamp.coffee.domain.orders.dto;

import java.util.List;

import com.ucamp.coffee.domain.orders.type.OrderType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class OrdersCreateDTO {

	private Long storeId;
	private Long memberSubscriptionId;
	private OrderType orderType;
	private List<MenuDTO> menu; 
	
	 @Getter
	    @Setter
	    @NoArgsConstructor
	    @AllArgsConstructor
	    public static class MenuDTO {
	        private Long menuId;
	        private int count;
	    }
}
