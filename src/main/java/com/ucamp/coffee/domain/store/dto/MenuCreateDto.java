package com.ucamp.coffee.domain.store.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MenuCreateDto {
    private Long partnerStoreId;
    private String menuName;
    private Integer price;
    private String menuImg;
    private String menuDesc;
    private String menuStatus;
    private String menuType;
}