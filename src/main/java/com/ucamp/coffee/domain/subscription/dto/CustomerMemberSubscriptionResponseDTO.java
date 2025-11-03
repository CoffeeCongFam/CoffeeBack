package com.ucamp.coffee.domain.subscription.dto;

import com.ucamp.coffee.domain.store.dto.CustomerStoreSimpleDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CustomerMemberSubscriptionResponseDTO {
    private Long subId;
    private CustomerStoreSimpleDTO store;
    private String subName;
    private String isGift;
    private String isExpired;
    private String subStart;
    private String subEnd;
    private Integer remainingCount;
    private Integer price;
    private String receiver;
    private String sender;
    private String subscriptionType;
    private List<String> menu;
    private List<String> usedAt;
}
