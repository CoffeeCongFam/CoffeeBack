package com.ucamp.coffee.domain.review.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateDTO {
    private Long memberId;
    private Long partnerStoreId;
    private Long subscriptionId;
    private String reviewContent;
    private Integer rating;
}
