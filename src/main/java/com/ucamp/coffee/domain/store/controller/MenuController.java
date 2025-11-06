package com.ucamp.coffee.domain.store.controller;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.domain.store.dto.MenuCreateDTO;
import com.ucamp.coffee.domain.store.dto.MenuUpdateDTO;
import com.ucamp.coffee.domain.store.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/stores/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> createMenuInfo(
        @RequestPart("data") MenuCreateDTO dto,
        @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        service.createMenuInfo(dto, file);
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
        @RequestPart("data") MenuUpdateDTO dto,
        @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        service.updateMenuInfo(menuId, dto, file);
        return ResponseMapper.successOf(null);
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<ApiResponse<?>> deleteMenuInfo(@PathVariable Long menuId) {
        service.deleteMenuInfo(menuId);
        return ResponseMapper.successOf(null);
    }
}
