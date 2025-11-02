package com.ucamp.coffee.domain.orders.event;

public record OrderCompletedEvent(Long memberId, Long storeId) {

}
