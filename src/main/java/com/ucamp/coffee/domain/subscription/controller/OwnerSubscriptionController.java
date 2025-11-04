package com.ucamp.coffee.domain.subscription.controller;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.common.security.MemberDetails;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionCreateDTO;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionStatusDTO;
import com.ucamp.coffee.domain.subscription.service.OwnerSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/owners/subscriptions")
public class OwnerSubscriptionController {
    private final OwnerSubscriptionService service;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createSubscriptionInfo(
        @AuthenticationPrincipal MemberDetails user,
        @RequestBody SubscriptionCreateDTO dto)
    {
        service.createSubscriptionInfo(dto, user.getMemberId());
        return ResponseMapper.successOf(null);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> readSubscriptionList(@AuthenticationPrincipal MemberDetails user) {
        return ResponseMapper.successOf(service.readSubscriptionList(user.getMemberId()));
    }

    @GetMapping("/{subscriptionId}")
    public ResponseEntity<ApiResponse<?>> readSubscriptionInfo(@PathVariable Long subscriptionId) {
        return ResponseMapper.successOf(service.readSubscriptionInfo(subscriptionId));
    }

    @PatchMapping("/{subscriptionId}")
    public ResponseEntity<ApiResponse<?>> updateSubscriptionStatus(
        @AuthenticationPrincipal MemberDetails user,
        @PathVariable Long subscriptionId,
        @RequestBody SubscriptionStatusDTO dto
    ) {
        service.updateSubscriptionStatus(subscriptionId, dto, 1L);
        return ResponseMapper.successOf(null);
    }

    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<ApiResponse<?>> deleteSubscriptionInfo(
        @AuthenticationPrincipal MemberDetails user,
        @PathVariable Long subscriptionId
    ) {
        service.deleteSubscriptionInfo(subscriptionId, user.getMemberId());
        return ResponseMapper.successOf(null);
    }
}
