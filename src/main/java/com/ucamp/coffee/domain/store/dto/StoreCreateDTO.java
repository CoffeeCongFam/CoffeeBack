package com.ucamp.coffee.domain.store.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StoreCreateDTO {
    private String businessNumber;
    private String storeName;
    private String roadAddress;
    private String detailAddress;
    private String detailInfo;
    private String storeImg;
    private String storeTel;

    @JsonProperty("xPoint") private Double xPoint;
    @JsonProperty("yPoint") private Double yPoint;
}