package com.ucamp.coffee.domain.subscription.dto;

import com.ucamp.coffee.domain.store.dto.CustomerStoreSimpleDTO;
import com.ucamp.coffee.domain.store.dto.MenuResponseDTO;
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
    private Long receiverId;
    private Long senderId;
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
    private List<MenuResponseDTO> menu;
    private Long purchaseId;
    private List<String> usedAt;
    private List<String> refundReasons;
    private String refundedAt;
}
