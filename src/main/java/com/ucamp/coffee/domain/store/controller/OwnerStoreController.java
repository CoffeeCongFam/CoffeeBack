package com.ucamp.coffee.domain.store.controller;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.common.security.MemberDetails;
import com.ucamp.coffee.domain.store.dto.MenuCreateDTO;
import com.ucamp.coffee.domain.store.dto.StoreCreateDTO;
import com.ucamp.coffee.domain.store.dto.StoreUpdateDTO;
import com.ucamp.coffee.domain.store.service.OwnerStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owners/stores")
@RequiredArgsConstructor
public class OwnerStoreController {
    private final OwnerStoreService service;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createStore(
        @AuthenticationPrincipal MemberDetails user,
        @RequestBody StoreCreateDTO dto
    ) {
        service.createStoreInfo(dto, user.getMemberId());
        return ResponseMapper.successOf(null);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> readStoreInfo(@AuthenticationPrincipal MemberDetails user) {
        return ResponseMapper.successOf(service.readStoreInfo(user.getMemberId()));
    }

    @PatchMapping("/{partnerStoreId}")
    public ResponseEntity<ApiResponse<?>> updateStoreInfo(
        @AuthenticationPrincipal MemberDetails user,
        @PathVariable Long partnerStoreId,
        @RequestBody StoreUpdateDTO dto
    ) {
        service.updateStoreInfo(partnerStoreId, dto, user.getMemberId());
        return ResponseMapper.successOf(null);
    }

    @PostMapping("/menus")
    public ResponseEntity<ApiResponse<?>> createMenuInfo(@RequestBody MenuCreateDTO dto) {
        return ResponseMapper.successOf(null);
    }
}
