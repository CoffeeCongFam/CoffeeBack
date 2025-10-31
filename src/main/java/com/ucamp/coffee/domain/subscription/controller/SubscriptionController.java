package com.ucamp.coffee.domain.subscription.controller;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionCreateDto;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionStatusDto;
import com.ucamp.coffee.domain.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {
    private final SubscriptionService service;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createSubscriptionInfo(
        @RequestHeader(value = "Authorization", required = false) String accessToken,
        @RequestBody SubscriptionCreateDto dto
    ) {
        service.createSubscriptionInfo(accessToken, dto);
        return ResponseMapper.successOf(null);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> readSubscriptionList(
        @RequestHeader(value = "Authorization", required = false) String accessToken
    ) {
        return ResponseMapper.successOf(service.readSubscriptionList(accessToken));
    }

    @GetMapping("/{subscriptionId}")
    public ResponseEntity<ApiResponse<?>> readSubscriptionInfo(
        @PathVariable Long subscriptionId
    ) {
        return ResponseMapper.successOf(service.readSubscriptionInfo(subscriptionId));
    }

    @PatchMapping("/{subscriptionId}")
    public ResponseEntity<ApiResponse<?>> updateSubscriptionStatus(
        @RequestHeader(value = "Authorization", required = false) String accessToken,
        @PathVariable Long subscriptionId,
        @RequestBody SubscriptionStatusDto dto
    ) {
        service.updateSubscriptionStatus(accessToken, subscriptionId, dto);
        return ResponseMapper.successOf(null);
    }
}
