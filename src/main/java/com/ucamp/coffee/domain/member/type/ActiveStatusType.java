package com.ucamp.coffee.domain.member.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActiveStatusType {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    WITHDRAW("탈퇴");

    private final String description;
}