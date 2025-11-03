package com.ucamp.coffee.domain.store.entity;

import com.ucamp.coffee.common.entity.BaseEntity;
import com.ucamp.coffee.domain.store.dto.StoreHoursBatchUpsertDTO;
import com.ucamp.coffee.domain.store.type.DayOfWeekType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "STORE_HOURS")
public class StoreHours extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeHoursId;

    @Column(nullable = false)
    private String isClosed;

    private String openTime;

    private String closeTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeekType dayOfWeek;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_store_id", nullable = false)
    private Store store;

    public void update(StoreHoursBatchUpsertDTO.DayHoursDto dto) {
        if (dto.getIsClosed() != null && !isClosed.isBlank()) this.isClosed = dto.getIsClosed();
        if (dto.getOpenTime() != null) this.openTime = dto.getOpenTime();
        if (dto.getCloseTime() != null) this.closeTime = dto.getCloseTime();
    }
}