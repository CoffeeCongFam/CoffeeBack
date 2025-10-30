package com.ucamp.coffee.domain.store.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StoreUpdateDto {
    private String storeName;
    private String roadAddress;
    private String detailAddress;
    private String detailInfo;
    private String storeImg;
    private String storeTel;
    private String tel;

    @JsonProperty("xPoint") private Double xPoint;
    @JsonProperty("yPoint") private Double yPoint;
}
