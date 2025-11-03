package com.ucamp.coffee.domain.review.controller;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.common.security.MemberDetails;
import com.ucamp.coffee.domain.review.dto.ReviewCreateDTO;
import com.ucamp.coffee.domain.review.dto.ReviewUpdateDTO;
import com.ucamp.coffee.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService service;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createReviewInfo(
        @AuthenticationPrincipal MemberDetails user,
        @RequestBody ReviewCreateDTO dto
    ) {
        service.createReviewInfo(dto, user.getMemberId());
        return ResponseMapper.successOf(null);
    }

    @GetMapping("/stores/{partnerStoreId}")
    public ResponseEntity<ApiResponse<?>> readReviewListByStore(@PathVariable Long partnerStoreId) {
        return ResponseMapper.successOf(service.readReviewListByStore(partnerStoreId));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> readMyReviews(@AuthenticationPrincipal MemberDetails user) {
        return ResponseMapper.successOf(service.readMyReviews(user.getMemberId()));
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<?>> updateReviewInfo(
        @PathVariable Long reviewId,
        @RequestBody ReviewUpdateDTO dto
    ) {
        service.updateReviewInfo(reviewId, dto);
        return ResponseMapper.successOf(null);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<?>> deleteReviewInfo(@PathVariable Long reviewId) {
        service.deleteReviewInfo(reviewId);
        return ResponseMapper.successOf(null);
    }
}
