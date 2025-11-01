package com.ucamp.coffee.domain.store.mapper;

import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.store.dto.*;
import com.ucamp.coffee.domain.store.dto.CustomerStoreResponseDto;
import com.ucamp.coffee.domain.store.dto.OwnerStoreResponseDto;
import com.ucamp.coffee.domain.store.dto.StoreCreateDto;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.entity.StoreHours;
import com.ucamp.coffee.domain.subscription.dto.CustomerSubscriptionResponseDto;

import java.util.List;

public class StoreMapper {
    public static Store toEntity(StoreCreateDto dto, Member member) {
        return Store.builder()
            .member(member)
            .businessNumber(dto.getBusinessNumber())
            .storeName(dto.getStoreName())
            .roadAddress(dto.getRoadAddress())
            .detailAddress(dto.getDetailAddress())
            .detailInfo(dto.getDetailInfo())
            .storeImg(dto.getStoreImg())
            .storeTel(dto.getStoreTel())
            .xPoint(dto.getXPoint())
            .yPoint(dto.getYPoint())
            .build();
    }

    public static OwnerStoreResponseDto toOwnerStoreResponseDto(List<StoreHours> storeHours, Store store, Member member) {
        return OwnerStoreResponseDto.builder()
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

    public static CustomerStoreResponseDto toCustomerStoreDto(List<StoreHours> storeHours, Store store, List<MenuResponseDto> menus, List<CustomerSubscriptionResponseDto> subscriptions) {
        return CustomerStoreResponseDto.builder()
            .partnerStoreId(store.getPartnerStoreId())
            .storeName(store.getStoreName())
            .storeTel(store.getStoreTel())
            .roadAddress(store.getRoadAddress())
            .detailAddress(store.getDetailAddress())
            .detailInfo(store.getDetailInfo())
            .storeHours(toStoreHoursResponseDto(storeHours))
            .menus(menus)
            .subscriptions(subscriptions)
            .build();
    }

    private static List<StoreHoursResponseDto> toStoreHoursResponseDto(List<StoreHours> storeHours) {
        return storeHours.stream()
            .map(sh -> new StoreHoursResponseDto(
                sh.getDayOfWeek().name(),
                sh.getOpenTime(),
                sh.getCloseTime(),
                sh.getIsClosed()
            ))
            .toList();
    }
}