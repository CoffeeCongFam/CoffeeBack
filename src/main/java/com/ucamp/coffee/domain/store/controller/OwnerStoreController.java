package com.ucamp.coffee.domain.store.controller;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.domain.store.dto.MenuCreateDto;
import com.ucamp.coffee.domain.store.dto.StoreCreateDto;
import com.ucamp.coffee.domain.store.dto.StoreUpdateDto;
import com.ucamp.coffee.domain.store.service.OwnerStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owners/stores")
@RequiredArgsConstructor
public class OwnerStoreController {
    private final OwnerStoreService service;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createStore(@RequestBody StoreCreateDto dto) {
        service.createStoreInfo(dto);
        return ResponseMapper.successOf(null);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> readStoreInfo() {
        return ResponseMapper.successOf(service.readStoreInfo());
    }

    @PatchMapping("/{partnerStoreId}")
    public ResponseEntity<ApiResponse<?>> updateStoreInfo(
        @PathVariable Long partnerStoreId,
        @RequestBody StoreUpdateDto dto
    ) {
        service.updateStoreInfo(partnerStoreId, dto);
        return ResponseMapper.successOf(null);
    }

    @PostMapping("/menus")
    public ResponseEntity<ApiResponse<?>> createMenuInfo(@RequestBody MenuCreateDto dto) {
        return ResponseMapper.successOf(null);
    }
}
