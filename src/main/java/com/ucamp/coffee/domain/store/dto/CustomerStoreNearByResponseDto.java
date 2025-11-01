package com.ucamp.coffee.domain.store.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerStoreNearByResponseDto {
    private Long storeId;
    private String storeName;
    private String storeStatus;
    private String storeImage;
    private String roadAddress;
    private String detailAddress;
    private Double xPoint;
    private Double yPoint;
    private Integer subscriptionStock;
    private Integer subscriberCount;
    private Boolean isSubscribed;
    private Integer reviewCount;
    private Double averageRating;
}
