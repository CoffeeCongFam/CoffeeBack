package com.ucamp.coffee.domain.store.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MenuResponseDTO {
    private Long menuId;
    private Long partnerStoreId;
    private String menuType;
    private String menuName;
    private Integer price;
    private String menuImg;
    private String menuDesc;
    private String menuStatus;
    private String createdAt;
    private String updatedAt;
}