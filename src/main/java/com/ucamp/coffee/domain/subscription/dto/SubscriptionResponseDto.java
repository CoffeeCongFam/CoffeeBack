package com.ucamp.coffee.domain.subscription.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SubscriptionResponseDto {
    private Long subscriptionId;
    private Long partnerStoreId;
    private String subscriptionName;
    private Integer price;
    private String subscriptionDesc;
    private Integer totalSale;
    private String subscriptionImg;
    private Integer salesLimitQuantity;
    private String subscriptionType;
    private Integer subscriptionPeriod;
    private Integer maxDailyUsage;
    private Integer remainSalesQuantity;
    private String subscriptionStatus;
}