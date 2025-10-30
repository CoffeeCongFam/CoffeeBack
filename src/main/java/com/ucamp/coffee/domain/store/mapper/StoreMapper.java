package com.ucamp.coffee.domain.store.mapper;

import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.store.dto.StoreCreateDto;
import com.ucamp.coffee.domain.store.entity.Store;

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
}