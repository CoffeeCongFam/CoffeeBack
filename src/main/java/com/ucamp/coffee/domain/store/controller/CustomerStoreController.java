package com.ucamp.coffee.domain.store.controller;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.domain.store.service.CustomerStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/customers/stores")
@RequiredArgsConstructor
public class CustomerStoreController {
    private final CustomerStoreService service;

    @GetMapping("/{partnerStoreId}")
    public ResponseEntity<ApiResponse<?>> readStoreInfo(@PathVariable Long partnerStoreId) {
        return ResponseMapper.successOf(service.readStoreInfo(partnerStoreId));
    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<?>> readNearbyStores(
        @RequestParam Double xPoint,
        @RequestParam Double yPoint,
        @RequestParam(defaultValue = "2") Double radius
    ) {
        return ResponseMapper.successOf(service.readNearbyStores(xPoint, yPoint, radius));
    }
}
