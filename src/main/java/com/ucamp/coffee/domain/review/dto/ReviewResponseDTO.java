package com.ucamp.coffee.domain.review.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDTO {
    private Long reviewId;
    private Long memberId;
    private String name;
    private Long partnerStoreId;
    private String partnerStoreName;
    private Long subscriptionId;
    private String subscriptionName;
    private String reviewContent;
    private Integer rating;
    private String reviewImg;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
