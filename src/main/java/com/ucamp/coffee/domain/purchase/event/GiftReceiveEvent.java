package com.ucamp.coffee.domain.purchase.event;

public record GiftReceiveEvent(Long purchaseId, Long memberSubscriptionId) {

}
