package com.ucamp.coffee.domain.store.controller;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.domain.store.dto.StoreCreateDto;
import com.ucamp.coffee.domain.store.dto.StoreResponseDto;
import com.ucamp.coffee.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService service;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createStore(@RequestHeader(value = "Authorization", required = false) String accessToken, @RequestBody StoreCreateDto dto) {
        service.createStoreInfo(dto, accessToken);
        return ResponseMapper.successOf(null);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> readStoreInfo(@RequestHeader(value = "Authorization", required = false) String accessToken) {
        StoreResponseDto dto = service.readStoreInfo(accessToken);

        log.info("======================================");
        log.info(dto.toString());
        log.info("======================================");

        return ResponseMapper.successOf(dto);
    }
}
