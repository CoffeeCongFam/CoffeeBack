package com.ucamp.coffee.domain.store.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IsClosedType {
    Y("휴무"),
    N("영업");

    private final String description;
}
