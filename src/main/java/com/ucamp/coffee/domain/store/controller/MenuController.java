package com.ucamp.coffee.domain.store.controller;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.domain.store.dto.MenuCreateDto;
import com.ucamp.coffee.domain.store.dto.MenuUpdateDto;
import com.ucamp.coffee.domain.store.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stores/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService service;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createMenuInfo(@RequestBody MenuCreateDto dto) {
        service.createMenuInfo(dto);
        return ResponseMapper.successOf(null);
    }

    @GetMapping("/store/{partnerStoreId}")
    public ResponseEntity<ApiResponse<?>> readMenuListByStore(@PathVariable Long partnerStoreId) {
        return ResponseMapper.successOf(service.readMenuListByStore(partnerStoreId));
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<ApiResponse<?>> readMenuInfo(@PathVariable Long menuId) {
        return ResponseMapper.successOf(service.readMenuInfo(menuId));
    }

    @PatchMapping("/{menuId}")
    public ResponseEntity<ApiResponse<?>> updateMenuInfo(
        @PathVariable Long menuId,
        @RequestBody MenuUpdateDto dto
    ) {
        service.updateMenuInfo(menuId, dto);
        return ResponseMapper.successOf(null);
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<ApiResponse<?>> deleteMenuInfo(@PathVariable Long menuId) {
        service.deleteMenuInfo(menuId);
        return ResponseMapper.successOf(null);
    }
}
