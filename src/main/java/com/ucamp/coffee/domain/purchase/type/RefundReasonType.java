package com.ucamp.coffee.domain.purchase.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RefundReasonType {

    OVER_PERIOD("결제 후 7일이 지나 환불이 불가합니다."),
    USED_ALREADY("이미 구독을 사용 중이어서 환불이 불가합니다."),
    EVENT_PRODUCT("이벤트 상품은 환불이 불가합니다."),
	ALREADY_REFUNDED("이미 환불된 상품입니다.");

    private final String description;
}