package com.ucamp.coffee.domain.subscription.dto;

import com.ucamp.coffee.domain.store.dto.CustomerStoreSimpleDTO;
import com.ucamp.coffee.domain.store.dto.MenuResponseDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CustomerSubscriptionResponseDTO {
    private Long subscriptionId;
    private CustomerStoreSimpleDTO store;
    private Long partnerStoreId;
    private String subscriptionName;
    private Integer price;
    private String subscriptionDesc;
    private Integer totalSale;
    private Integer salesLimitQuantity;
    private String subscriptionType;
    private Integer subscriptionPeriod;
    private Integer maxDailyUsage;
    private Integer remainSalesQuantity;
    private String subscriptionStatus;
    private String subscriptionImg;
    private List<MenuResponseDTO> menus;
    private String deletedAt;
}