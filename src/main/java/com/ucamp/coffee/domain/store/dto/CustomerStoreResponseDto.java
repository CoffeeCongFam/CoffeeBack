package com.ucamp.coffee.domain.store.dto;

import com.ucamp.coffee.domain.subscription.dto.SubscriptionResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CustomerStoreResponseDto {
    private Long partnerStoreId;
    private String storeName;
    private String storeTel;
    private String roadAddress;
    private String detailAddress;
    private String detailInfo;
    private List<StoreHoursResponseDto> storeHours;
    private List<MenuResponseDto> menus;
    private List<SubscriptionResponseDto> subscriptions;
}