package com.ucamp.coffee.domain.store.controller;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.domain.store.dto.StoreHoursBatchUpsertDto;
import com.ucamp.coffee.domain.store.service.OwnerStoreHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stores/storeHours")
@RequiredArgsConstructor
public class OwnerStoreHoursController {
    private final OwnerStoreHoursService service;

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<?>> upsertStoreHours(@RequestBody StoreHoursBatchUpsertDto dto) {
        service.upsertStoreHours(dto);
        return ResponseMapper.successOf(null);
    }
}
