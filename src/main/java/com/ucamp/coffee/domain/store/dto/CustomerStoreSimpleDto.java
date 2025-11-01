package com.ucamp.coffee.domain.store.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CustomerStoreSimpleDto {
    private Long partnerStoreId;
    private String storeName;
    private String storeImg;
}
