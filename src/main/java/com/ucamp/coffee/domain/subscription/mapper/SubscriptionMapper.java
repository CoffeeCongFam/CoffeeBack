package com.ucamp.coffee.domain.subscription.mapper;

import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionCreateDto;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionResponseDto;
import com.ucamp.coffee.domain.subscription.entity.Subscription;
import com.ucamp.coffee.domain.subscription.type.SubscriptionStatusType;
import com.ucamp.coffee.domain.subscription.type.SubscriptionType;

public class SubscriptionMapper {
    public static Subscription toEntity(SubscriptionCreateDto dto, Store store) {
        return Subscription.builder()
            .store(store)
            .subscriptionName(dto.getSubscriptionName())
            .price(dto.getPrice() != null ? dto.getPrice().intValue() : null)
            .subscriptionDesc(dto.getSubscriptionDesc())
            .totalSale(0)
            .subscriptionImg(dto.getSubscriptionImg())
            .salesLimitQuantity(dto.getSalesLimitQuantity() != null ? dto.getSalesLimitQuantity().intValue() : null)
            .subscriptionType(dto.getSubscriptionType() != null ? SubscriptionType.valueOf(dto.getSubscriptionType()) : null)
            .subscriptionPeriod(dto.getSubscriptionPeriod() != null ? dto.getSubscriptionPeriod().intValue() : null)
            .maxDailyUsage(dto.getMaxDailyUsage() != null ? dto.getMaxDailyUsage().intValue() : null)
            .remainSalesQuantity(dto.getSalesLimitQuantity() != null ? dto.getSalesLimitQuantity().intValue() : null)
            .subscriptionStatus(SubscriptionStatusType.ONSALE)
            .build();
    }

    public static SubscriptionResponseDto toResponseDto(Subscription subscription) {
        return SubscriptionResponseDto.builder()
            .subscriptionId(subscription.getSubscriptionId())
            .partnerStoreId(subscription.getStore().getPartnerStoreId())
            .subscriptionName(subscription.getSubscriptionName())
            .price(subscription.getPrice())
            .subscriptionDesc(subscription.getSubscriptionDesc())
            .totalSale(subscription.getTotalSale())
            .subscriptionImg(subscription.getSubscriptionImg())
            .salesLimitQuantity(subscription.getSalesLimitQuantity())
            .subscriptionType(subscription.getSubscriptionType() != null ? subscription.getSubscriptionType().name() : null)
            .subscriptionPeriod(subscription.getSubscriptionPeriod())
            .maxDailyUsage(subscription.getMaxDailyUsage())
            .remainSalesQuantity(subscription.getRemainSalesQuantity())
            .subscriptionStatus(subscription.getSubscriptionStatus() != null ? subscription.getSubscriptionStatus().name() : null)
            .build();
    }
}
