package com.ucamp.coffee.domain.orders.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusType {
    REQUEST("접수중"),
    INPROGRESS("제조중"),
    COMPLETED("제조완료"),
    RECEIVED("수령완료"),
    REJECTED("주문거부"),
    CANCELED("주문취소");

    private final String description;
}
