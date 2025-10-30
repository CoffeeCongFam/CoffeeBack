package com.ucamp.coffee.domain.store.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MenuType {

	DESSERT("디저트"),
	BEVERAGE("음료");
	
	private final String description;
}
