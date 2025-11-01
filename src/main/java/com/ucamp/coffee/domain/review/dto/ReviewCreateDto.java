package com.ucamp.coffee.domain.review.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateDto {
    private Long memberId;
    private Long partnerStoreId;
    private String reviewContent;
    private Integer rating;
    private String reviewImg;
}
