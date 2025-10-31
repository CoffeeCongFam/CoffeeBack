package com.ucamp.coffee.domain.store.controller;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.domain.store.dto.StoreCreateDto;
import com.ucamp.coffee.domain.store.dto.StoreUpdateDto;
import com.ucamp.coffee.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService service;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createStore(
        @RequestHeader(value = "Authorization", required = false) String accessToken,
        @RequestBody StoreCreateDto dto
    ) {
        service.createStoreInfo(accessToken, dto);
        return ResponseMapper.successOf(null);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> readStoreInfo(
        @RequestHeader(value = "Authorization", required = false) String accessToken
    ) {
        return ResponseMapper.successOf(service.readStoreInfo(accessToken));
    }

    @PatchMapping("/{partnerStoreId}")
    public ResponseEntity<ApiResponse<?>> updateStoreInfo(
        @PathVariable Long partnerStoreId,
        @RequestHeader(value = "Authorization", required = false) String accessToken,
        @RequestBody StoreUpdateDto dto
    ) {
        service.updateStoreInfo(partnerStoreId, accessToken, dto);
        return ResponseMapper.successOf(null);
    }
}
