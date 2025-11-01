package com.ucamp.coffee.domain.review.controller;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.domain.review.dto.ReviewCreateDto;
import com.ucamp.coffee.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService service;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createReviewInfo(@RequestBody ReviewCreateDto dto) {
        service.createReviewInfo(dto);
        return ResponseMapper.successOf(null);
    }

//    @GetMapping("/stores/{partnerStoreId}")
//    public ResponseEntity<ApiResponse<?>> readReviewListByStore(@RequestBody Long partnerStoreId) {
//        return ResponseMapper.successOf(service.readReviewListByStore(partnerStoreId));
//    }
}
