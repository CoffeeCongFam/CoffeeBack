package com.ucamp.coffee.domain.store.repository;

import com.ucamp.coffee.domain.store.entity.StoreHours;
import com.ucamp.coffee.domain.store.type.DayOfWeekType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreHoursRepository extends JpaRepository<StoreHours, Long> {
    List<StoreHours> findByStore_PartnerStoreIdInAndDayOfWeek(List<Long> partnerStoreIds, DayOfWeekType dayOfWeek);
}
