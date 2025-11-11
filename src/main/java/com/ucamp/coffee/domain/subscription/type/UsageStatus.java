package com.ucamp.coffee.domain.subscription.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UsageStatus {

	NOT_ACTIVATED("사용전"), 
	ACTIVE("사용중"),
	EXPIRED("만료");
	
	private final String description;
}
