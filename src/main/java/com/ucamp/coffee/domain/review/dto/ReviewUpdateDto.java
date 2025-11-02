package com.ucamp.coffee.domain.review.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewUpdateDto {
    private String reviewContent;
    private Integer rating;
    private String reviewImg;
}

