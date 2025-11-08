package com.ucamp.coffee.domain.orders.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdersListSearchDTO {

	private Long memberId;
	private String period;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private LocalDateTime lastCreatedAt;
}
