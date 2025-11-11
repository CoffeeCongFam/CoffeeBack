package com.ucamp.coffee.domain.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdersMessageDTO {

	private String storeName;
	private String name;
	private String tel;
	private int orderNumber;
}
