package com.ucamp.coffee.domain.subscription.mapper;

import com.ucamp.coffee.domain.store.dto.CustomerStoreSimpleDto;
import com.ucamp.coffee.domain.store.entity.Menu;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.subscription.dto.CustomerMemberSubscriptionResponseDto;
import com.ucamp.coffee.domain.subscription.dto.CustomerSubscriptionResponseDto;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionCreateDto;
import com.ucamp.coffee.domain.subscription.dto.OwnerSubscriptionResponseDto;
import com.ucamp.coffee.domain.subscription.entity.MemberSubscription;
import com.ucamp.coffee.domain.subscription.entity.Subscription;
import com.ucamp.coffee.domain.subscription.entity.SubscriptionUsageHistory;
import com.ucamp.coffee.domain.subscription.type.SubscriptionStatusType;
import com.ucamp.coffee.domain.subscription.type.SubscriptionType;

import java.util.List;

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

    public static OwnerSubscriptionResponseDto toOwnerResponseDto(Subscription subscription) {
        return OwnerSubscriptionResponseDto.builder()
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

    public static CustomerSubscriptionResponseDto toCustomerResponseDto(Subscription subscription, CustomerStoreSimpleDto dto) {
        return CustomerSubscriptionResponseDto.builder()
            .store(dto)
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

    public static CustomerMemberSubscriptionResponseDto toCustomerMemberResponseDto(MemberSubscription memberSubscription, List<Menu> menus, List<SubscriptionUsageHistory> subscriptionUsageHistories) {
        Subscription subscription = memberSubscription.getPurchase().getSubscription();
        Store store = subscription.getStore();

        return CustomerMemberSubscriptionResponseDto.builder()
            .subId(subscription.getSubscriptionId())
            .store(
                CustomerStoreSimpleDto.builder()
                    .partnerStoreId(store.getPartnerStoreId())
                    .storeName(store.getStoreName())
                    .storeImg(store.getStoreImg())
                    .build()
            )
            .subName(subscription.getSubscriptionName())
            .isGift(memberSubscription.getIsGift())
            .isExpired(memberSubscription.getUsageStatus().name())
            .subStart(memberSubscription.getSubscriptionStart())
            .subEnd(memberSubscription.getSubscriptionEnd())
            .remainingCount(memberSubscription.getDailyRemainCount())
            .menu(menus.stream().map(Menu::getMenuName).toList())
            .usedAt(subscriptionUsageHistories.stream().map(SubscriptionUsageHistory::getCreatedAt).sorted().toList())
            .build();
    }
}
