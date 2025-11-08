package com.ucamp.coffee.domain.store.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OwnerStoreResponseDTO {
    private Long partnerStoreId;
    private String storeName;
    private String storeTel;
    private String tel;
    private String roadAddress;
    private String detailAddress;
    private String businessNumber;
    private String detailInfo;
    private String storeImg;
    private List<StoreHoursResponseDTO> storeHours;
}