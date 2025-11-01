package com.ucamp.coffee.domain.review.mapper;

import com.ucamp.coffee.domain.review.dto.ReviewCreateDto;
import com.ucamp.coffee.domain.review.entity.Review;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.store.entity.Store;

public class ReviewMapper {
    public static Review toEntity(ReviewCreateDto dto, Member member, Store store) {
        return Review.builder()
            .member(member)
            .store(store)
            .reviewContent(dto.getReviewContent())
            .rating(dto.getRating())
            .reviewImg(dto.getReviewImg())
            .build();
    }

    /* TODO: 회원, 매장 패치 조인 필요 */
    public static ReviewCreateDto toDto(Review review) {
        return ReviewCreateDto.builder()
            .memberId(review.getMember().getMemberId())
            .partnerStoreId(review.getStore().getPartnerStoreId())
            .reviewContent(review.getReviewContent())
            .rating(review.getRating())
            .reviewImg(review.getReviewImg())
            .build();
    }
}

