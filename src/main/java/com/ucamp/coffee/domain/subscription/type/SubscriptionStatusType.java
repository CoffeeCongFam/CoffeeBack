package com.ucamp.coffee.domain.subscription.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubscriptionStatusType {
    ONSALE("판매중"),
    SOLDOUT("품절"),
    SUSPENDED("판매중지");

    private final String description;
}