package com.ucamp.coffee.domain.store.dto;

import com.ucamp.coffee.domain.subscription.dto.CustomerSubscriptionResponseDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CustomerStoreResponseDTO {
    private Long partnerStoreId;
    private String storeName;
    private String storeTel;
    private String roadAddress;
    private String detailAddress;
    private String detailInfo;
    private String storeImg;
    private List<StoreHoursResponseDTO> storeHours;
    private List<MenuResponseDTO> menus;
    private List<CustomerSubscriptionResponseDTO> subscriptions;
}