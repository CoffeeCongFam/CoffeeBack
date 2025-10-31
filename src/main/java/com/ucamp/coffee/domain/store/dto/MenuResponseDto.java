package com.ucamp.coffee.domain.store.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MenuResponseDto {
    private Long menuId;
    private Long partnerStoreId;
    private String menuType;
    private String menuName;
    private Integer price;
    private String menuImg;
    private String menuDesc;
    private String menuStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}