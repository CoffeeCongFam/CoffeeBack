package com.ucamp.coffee.domain.orders.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 점주가 과거 내역을 조회할 때 사용하는 요청 DTO
 */
@Data
@NoArgsConstructor
public class OrdersStorePastRequestDTO {

	private long partnerStoreId;
	private String searchDate;
}
