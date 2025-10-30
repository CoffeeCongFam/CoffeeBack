package com.ucamp.coffee.domain.member.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GenderType {
    F("여성"),
    M("남성");

    private final String description;
}