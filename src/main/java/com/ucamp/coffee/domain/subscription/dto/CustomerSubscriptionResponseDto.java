package com.ucamp.coffee.domain.subscription.dto;

import com.ucamp.coffee.domain.store.dto.CustomerStoreSimpleDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CustomerSubscriptionResponseDto {
    private Long subscriptionId;
    private CustomerStoreSimpleDto store;
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