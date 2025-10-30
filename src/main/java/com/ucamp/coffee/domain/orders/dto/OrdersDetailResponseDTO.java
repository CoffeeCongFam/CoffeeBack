package com.ucamp.coffee.domain.orders.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdersDetailResponseDTO {

    private Long orderId;
    private String orderStatus;
    private StoreDTO store;
    private SubscriptionDTO subscription;
    private List<MenuDTO> menuList;
    private Integer totalQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime canceledAt;

    // 매장 정보
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StoreDTO {
        private Long storeId;
        private String storeName;
    }

    // 구독 정보
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubscriptionDTO {
        private Long subscriptionId;
        private String subscriptionType;
    }

    // 주문 메뉴 리스트
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MenuDTO {
        private Long menuId;
        private String menuName;
        private String menuType;
        private Integer quantity;
    }
}
