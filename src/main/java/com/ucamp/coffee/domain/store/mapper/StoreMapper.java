package com.ucamp.coffee.domain.store.mapper;

import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.store.dto.*;
import com.ucamp.coffee.domain.store.dto.CustomerStoreResponseDTO;
import com.ucamp.coffee.domain.store.dto.OwnerStoreResponseDTO;
import com.ucamp.coffee.domain.store.dto.StoreCreateDTO;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.entity.StoreHours;
import com.ucamp.coffee.domain.subscription.dto.CustomerSubscriptionResponseDTO;

import java.util.List;

public class StoreMapper {
    public static Store toEntity(StoreCreateDTO dto, Member member, String imageUrl) {
        return Store.builder()
                .member(member)
                .businessNumber(dto.getBusinessNumber())
                .storeName(dto.getStoreName())
                .roadAddress(dto.getRoadAddress())
                .detailAddress(dto.getDetailAddress())
                .detailInfo(dto.getDetailInfo())
                .storeImg(imageUrl)
                .storeTel(dto.getStoreTel())
                .xPoint(dto.getXPoint())
                .yPoint(dto.getYPoint())
                .build();
    }

    public static OwnerStoreResponseDTO toOwnerStoreResponseDto(List<StoreHours> storeHours, Store store, Member member) {
        return OwnerStoreResponseDTO.builder()
                .partnerStoreId(store.getPartnerStoreId())
                .storeName(store.getStoreName())
                .storeTel(store.getStoreTel())
                .tel(member.getTel())
                .roadAddress(store.getRoadAddress())
                .detailAddress(store.getDetailAddress())
                .businessNumber(store.getBusinessNumber())
                .detailInfo(store.getDetailInfo())
                .storeHours(toStoreHoursResponseDto(storeHours))
                .build();
    }

    public static CustomerStoreResponseDTO toCustomerStoreDto(List<StoreHours> storeHoursList, Store store, List<MenuResponseDTO> menus, List<CustomerSubscriptionResponseDTO> subscriptions) {
        return CustomerStoreResponseDTO.builder()
                .partnerStoreId(store.getPartnerStoreId())
                .storeName(store.getStoreName())
                .storeTel(store.getStoreTel())
                .roadAddress(store.getRoadAddress())
                .detailAddress(store.getDetailAddress())
                .detailInfo(store.getDetailInfo())
                .storeHours(toStoreHoursResponseDto(storeHoursList))
                .menus(menus)
                .subscriptions(subscriptions)
                .build();
    }

    private static List<StoreHoursResponseDTO> toStoreHoursResponseDto(List<StoreHours> storeHours) {
        return storeHours.stream()
                .map(sh -> new StoreHoursResponseDTO(
                        sh.getDayOfWeek().name(),
                        sh.getOpenTime(),
                        sh.getCloseTime(),
                        sh.getIsClosed()
                ))
                .toList();
    }
}