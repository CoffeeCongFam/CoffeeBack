package com.ucamp.coffee.domain.review.entity;

import com.ucamp.coffee.common.entity.BaseEntity;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.review.dto.ReviewUpdateDTO;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.subscription.entity.Subscription;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "REVIEW")
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @Column(length = 100, nullable = false)
    private String reviewContent;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 200)
    private String reviewImg;

    public void update(ReviewUpdateDTO dto) {
        if (dto.getReviewContent() != null && !dto.getReviewContent().isBlank()) {
            this.reviewContent = dto.getReviewContent();
        }

        if (dto.getRating() != null) {
            this.rating = dto.getRating();
        }

        if (dto.getReviewImg() != null) {
            this.reviewImg = dto.getReviewImg();
        }
    }
}
