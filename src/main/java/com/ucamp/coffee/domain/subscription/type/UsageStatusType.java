package com.ucamp.coffee.domain.subscription.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UsageStatusType {
    NOT_ACTIVATED("미사용"),
    ACTIVATED("사용"),
    EXPIRED("만료");

    private final String description;
}
