package com.ucamp.coffee.domain.purchase.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {

	PAID("결제됨"),
	DENIED("거부됨"),
	REFUNDED("환불됨");
	
	private final String description;
}
