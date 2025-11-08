package com.ucamp.coffee.domain.orders.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdersMenuResponseDTO {
    private Long menuId;
    private String menuName;
    private Integer quantity;
}
