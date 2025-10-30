package com.ucamp.coffee.domain.member.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberType {
    GENERAL("일반"),
    STORE("점주");

    private final String description;
}