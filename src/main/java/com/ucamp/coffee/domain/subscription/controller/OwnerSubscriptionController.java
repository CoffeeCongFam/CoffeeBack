package com.ucamp.coffee.domain.subscription.controller;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.common.security.MemberDetails;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionCreateDTO;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionStatusDTO;
import com.ucamp.coffee.domain.subscription.service.OwnerSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/owners/subscriptions")
public class OwnerSubscriptionController {
    private final OwnerSubscriptionService service;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> createSubscriptionInfo(
            // @AuthenticationPrincipal MemberDetails user,
            @RequestPart("data") SubscriptionCreateDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        service.createSubscriptionInfo(dto, file, 1L);
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
