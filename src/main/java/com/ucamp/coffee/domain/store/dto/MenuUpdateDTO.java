package com.ucamp.coffee.domain.store.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MenuUpdateDTO {
    private String menuName;
    private Integer price;
    private String menuDesc;
    private String menuStatus;
    private String menuType;
}