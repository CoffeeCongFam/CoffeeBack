package com.ucamp.coffee.domain.store.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StoreHoursResponseDTO {
    private String dayOfWeek;
    private String openTime;
    private String closeTime;
    private String isClosed;
}
