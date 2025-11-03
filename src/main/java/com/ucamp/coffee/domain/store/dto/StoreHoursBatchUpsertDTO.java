package com.ucamp.coffee.domain.store.dto;

import com.ucamp.coffee.domain.store.type.DayOfWeekType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreHoursBatchUpsertDTO {
    private Long partnerStoreId;
    private List<DayHoursDto> dayHours;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DayHoursDto {
        private DayOfWeekType dayOfWeek;
        private String isClosed;
        private String openTime;
        private String closeTime;
        private Long storeHoursId;
    }
}
