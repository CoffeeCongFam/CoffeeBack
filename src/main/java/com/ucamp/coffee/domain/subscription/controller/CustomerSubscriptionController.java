package com.ucamp.coffee.domain.subscription.controller;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.common.security.MemberDetails;
import com.ucamp.coffee.domain.subscription.service.CustomerSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers/subscriptions")
public class CustomerSubscriptionController {
    private final CustomerSubscriptionService service;

    @GetMapping("/stores/{partnerStoreId}")
    public ResponseEntity<ApiResponse<?>> readSubscriptionList(
            @AuthenticationPrincipal MemberDetails user,
            @PathVariable Long partnerStoreId) {
        return ResponseMapper.successOf(service.readSubscriptionList(user.getMemberId()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> readMemberSubscriptionList(
        @AuthenticationPrincipal MemberDetails user
    ) {
        return ResponseMapper.successOf(service.readMemberSubscriptionList(user.getMemberId()));
    }
}
