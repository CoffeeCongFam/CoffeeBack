package com.ucamp.coffee.domain.store.entity;

import com.ucamp.coffee.domain.store.type.DayOfWeekType;
import com.ucamp.coffee.domain.store.type.IsClosedType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "STORE_HOURS")
public class StoreHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeHoursId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IsClosedType isClosed;

    private LocalDateTime openTime;

    private LocalDateTime closeTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeekType dayOfWeek;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_store_id", nullable = false)
    private Store store;
}