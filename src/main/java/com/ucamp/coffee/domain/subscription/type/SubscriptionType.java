package com.ucamp.coffee.domain.subscription.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubscriptionType {
    BASIC("베이직"),
    STANDARD("스탠다드"),
    PREMIUM("프리미엄");

    private final String description;
}
