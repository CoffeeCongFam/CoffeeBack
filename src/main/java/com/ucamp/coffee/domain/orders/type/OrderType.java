package com.ucamp.coffee.domain.orders.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderType {
    IN("매장"),
    OUT("포장");

    private final String description;
}
