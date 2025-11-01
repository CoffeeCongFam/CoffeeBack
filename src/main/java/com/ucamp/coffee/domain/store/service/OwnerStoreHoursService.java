package com.ucamp.coffee.domain.store.service;

import com.ucamp.coffee.domain.store.dto.StoreHoursBatchUpsertDto;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.entity.StoreHours;
import com.ucamp.coffee.domain.store.repository.StoreHoursRepository;
import com.ucamp.coffee.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OwnerStoreHoursService {
    private final StoreHoursRepository storeHoursRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public void upsertStoreHours(StoreHoursBatchUpsertDto dto) {
        Store store = storeRepository.findById(dto.getPartnerStoreId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장입니다."));

        for (StoreHoursBatchUpsertDto.DayHoursDto dayDto : dto.getDayHours()) {
            StoreHours storeHours;

            if (dayDto.getStoreHoursId() != null) {
                storeHours = storeHoursRepository.findById(dayDto.getStoreHoursId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 영업 시간 데이터입니다."));
                storeHours.update(dayDto);
            } else {
                storeHours = StoreHours.builder()
                    .store(store)
                    .dayOfWeek(dayDto.getDayOfWeek())
                    .isClosed(dayDto.getIsClosed())
                    .openTime(dayDto.getOpenTime())
                    .closeTime(dayDto.getCloseTime())
                    .build();
            }

            storeHours.setIsClosed(dayDto.getIsClosed());
            storeHours.setOpenTime(dayDto.getOpenTime());
            storeHours.setCloseTime(dayDto.getCloseTime());

            storeHoursRepository.save(storeHours);
        }
    }
}
