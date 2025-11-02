package com.ucamp.coffee.domain.review.mapper;

import com.ucamp.coffee.domain.review.dto.ReviewCreateDto;
import com.ucamp.coffee.domain.review.dto.ReviewResponseDto;
import com.ucamp.coffee.domain.review.entity.Review;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.subscription.entity.Subscription;

public class ReviewMapper {
    public static Review toEntity(ReviewCreateDto dto, Member member, Store store, Subscription subscription) {
        return Review.builder()
            .member(member)
            .store(store)
            .subscription(subscription)
            .reviewContent(dto.getReviewContent())
            .rating(dto.getRating())
            .reviewImg(dto.getReviewImg())
            .build();
    }

    public static ReviewResponseDto toDto(Review review) {
        return ReviewResponseDto.builder()
            .reviewId(review.getReviewId())
            .memberId(review.getMember().getMemberId())
            .name(review.getMember().getName())
            .partnerStoreId(review.getStore().getPartnerStoreId())
            .partnerStoreName(review.getStore().getStoreName())
            .subscriptionId(review.getSubscription() != null ? review.getSubscription().getSubscriptionId() : null)
            .subscriptionName(review.getSubscription() != null ? review.getSubscription().getSubscriptionName() : null)
            .reviewContent(review.getReviewContent())
            .rating(review.getRating())
            .reviewImg(review.getReviewImg())
            .createdAt(review.getCreatedAt() != null ? review.getCreatedAt().toLocalDate() : null)
            .updatedAt(review.getUpdatedAt() != null ? review.getUpdatedAt().toLocalDate() : null)
            .build();
    }
}

