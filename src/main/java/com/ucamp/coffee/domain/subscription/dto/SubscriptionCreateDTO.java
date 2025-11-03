package com.ucamp.coffee.domain.subscription.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SubscriptionCreateDTO {
    private Long partnerStoreId;
    private String subscriptionName;
    private Long price;
    private String subscriptionDesc;
    private String subscriptionImg;
    private Long salesLimitQuantity;
    private String subscriptionType;
    private Long subscriptionPeriod;
    private Long maxDailyUsage;
    private List<Long> menuIds;
}