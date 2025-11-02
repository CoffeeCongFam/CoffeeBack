package com.ucamp.coffee.domain.subscription.dto;

import com.ucamp.coffee.domain.store.dto.CustomerStoreSimpleDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CustomerMemberSubscriptionResponseDto {
    private Long subId;
    private CustomerStoreSimpleDto store;
    private String subName;
    private String isGift;
    private String isExpired;
    private String subStart;
    private String subEnd;
    private Integer remainingCount;
    private List<String> menu;
    private List<String> usedAt;
}
