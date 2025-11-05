package com.ucamp.coffee.domain.subscription.mapper;

import com.ucamp.coffee.common.util.DateTimeUtil;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.purchase.entity.Purchase;
import com.ucamp.coffee.domain.store.dto.CustomerStoreSimpleDTO;
import com.ucamp.coffee.domain.store.dto.MenuResponseDTO;
import com.ucamp.coffee.domain.store.entity.Menu;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.mapper.MenuMapper;
import com.ucamp.coffee.domain.subscription.dto.CustomerMemberSubscriptionResponseDTO;
import com.ucamp.coffee.domain.subscription.dto.CustomerSubscriptionResponseDTO;
import com.ucamp.coffee.domain.subscription.dto.OwnerSubscriptionResponseDTO;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionCreateDTO;
import com.ucamp.coffee.domain.subscription.entity.MemberSubscription;
import com.ucamp.coffee.domain.subscription.entity.Subscription;
import com.ucamp.coffee.domain.subscription.entity.SubscriptionUsageHistory;
import com.ucamp.coffee.domain.subscription.type.SubscriptionStatusType;
import com.ucamp.coffee.domain.subscription.type.SubscriptionType;

import java.util.List;

public class SubscriptionMapper {
    public static Subscription toEntity(SubscriptionCreateDTO dto, Store store, String imageUrl) {
        return Subscription.builder()
                .store(store)
                .subscriptionName(dto.getSubscriptionName())
                .price(dto.getPrice() != null ? dto.getPrice().intValue() : null)
                .subscriptionDesc(dto.getSubscriptionDesc())
                .totalSale(0)
                .subscriptionImg(imageUrl)
                .salesLimitQuantity(dto.getSalesLimitQuantity() != null ? dto.getSalesLimitQuantity().intValue() : null)
                .subscriptionType(dto.getSubscriptionType() != null ? SubscriptionType.valueOf(dto.getSubscriptionType()) : null)
                .subscriptionPeriod(dto.getSubscriptionPeriod() != null ? dto.getSubscriptionPeriod().intValue() : null)
                .maxDailyUsage(dto.getMaxDailyUsage() != null ? dto.getMaxDailyUsage().intValue() : null)
                .remainSalesQuantity(dto.getSalesLimitQuantity() != null ? dto.getSalesLimitQuantity().intValue() : null)
                .subscriptionStatus(SubscriptionStatusType.ONSALE)
                .build();
    }

    public static OwnerSubscriptionResponseDTO toOwnerResponseDto(Subscription subscription, List<MenuResponseDTO> menus) {
    	Store store = subscription.getStore();
    	
        return OwnerSubscriptionResponseDTO.builder()
                .subscriptionId(subscription.getSubscriptionId())
                .partnerStoreId(store.getPartnerStoreId())
                .storeName(store.getStoreName())
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
                .menus(menus)
                .build();
    }

    public static CustomerSubscriptionResponseDTO toCustomerResponseDto(Subscription subscription, CustomerStoreSimpleDTO dto, List<MenuResponseDTO> menus) {
        return CustomerSubscriptionResponseDTO.builder()
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
                .menus(menus)
                .build();
    }

    public static CustomerMemberSubscriptionResponseDTO toCustomerMemberResponseDto(MemberSubscription memberSubscription, List<Menu> menus, List<SubscriptionUsageHistory> subscriptionUsageHistories) {
        Purchase purchase = memberSubscription.getPurchase();
        Subscription subscription = purchase.getSubscription();
        Store store = subscription.getStore();
        Member member = memberSubscription.getMember();
        Member buyer = purchase.getBuyer();
        List<MenuResponseDTO> menuDtos = menus.stream()
            .map(MenuMapper::toDto)
            .toList();

        return CustomerMemberSubscriptionResponseDTO.builder()
                .memberSubscriptionId(memberSubscription.getMemberSubscriptionId())
                .subId(subscription.getSubscriptionId())
                .receiverId(member.getMemberId())
                .senderId(buyer.getMemberId())
                .store(
                        CustomerStoreSimpleDTO.builder()
                                .partnerStoreId(store.getPartnerStoreId())
                                .storeName(store.getStoreName())
                                .storeImg(store.getStoreImg())
                                .build()
                )
                .subName(subscription.getSubscriptionName())
                .isGift(memberSubscription.getIsGift())
                .isExpired(memberSubscription.getUsageStatus().name())
                .subStart(DateTimeUtil.toUtcDateTime(memberSubscription.getSubscriptionStart()))
                .subEnd(DateTimeUtil.toUtcDateTime(memberSubscription.getSubscriptionEnd()))
                .remainingCount(memberSubscription.getDailyRemainCount())
                .price(subscription.getPrice())
                .receiver(member.getName())
                .sender(buyer.getName())
                .subscriptionType(subscription.getSubscriptionType().name())
                .menu(menuDtos)
                .purchaseId(purchase.getPurchaseId())
                .usedAt(subscriptionUsageHistories.stream().map(SubscriptionUsageHistory::getCreatedAt).map(DateTimeUtil::toUtcDateTime).sorted().toList())
                .refundReasons(null)
                .refundedAt(DateTimeUtil.toUtcDateTime(purchase.getRefundedAt()))
                .build();
    }
}
