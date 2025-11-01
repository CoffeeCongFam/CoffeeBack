package com.ucamp.coffee.domain.subscription.controller;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionCreateDto;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionStatusDto;
import com.ucamp.coffee.domain.subscription.service.OwnerSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/owners/subscriptions")
public class OwnerSubscriptionController {
    private final OwnerSubscriptionService service;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createSubscriptionInfo(@RequestBody SubscriptionCreateDto dto) {
        service.createSubscriptionInfo(dto);
        return ResponseMapper.successOf(null);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> readSubscriptionList() {
        return ResponseMapper.successOf(service.readSubscriptionList());
    }

    @GetMapping("/{subscriptionId}")
    public ResponseEntity<ApiResponse<?>> readSubscriptionInfo(@PathVariable Long subscriptionId) {
        return ResponseMapper.successOf(service.readSubscriptionInfo(subscriptionId));
    }

    @PatchMapping("/{subscriptionId}")
    public ResponseEntity<ApiResponse<?>> updateSubscriptionStatus(
        @PathVariable Long subscriptionId,
        @RequestBody SubscriptionStatusDto dto
    ) {
        service.updateSubscriptionStatus(subscriptionId, dto);
        return ResponseMapper.successOf(null);
    }
}
