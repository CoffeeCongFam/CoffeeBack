package com.ucamp.coffee.domain.store.mapper;

import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.store.dto.StoreCreateDto;
import com.ucamp.coffee.domain.store.dto.StoreHoursResponseDto;
import com.ucamp.coffee.domain.store.dto.StoreResponseDto;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.entity.StoreHours;

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

    public static StoreResponseDto toStoreResponseDto(List<StoreHours> storeHours, Store store, Member member) {
        return StoreResponseDto.builder()
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