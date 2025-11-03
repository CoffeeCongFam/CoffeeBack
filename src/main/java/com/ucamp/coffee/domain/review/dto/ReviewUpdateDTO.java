package com.ucamp.coffee.domain.review.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewUpdateDTO {
    private String reviewContent;
    private Integer rating;
    private String reviewImg;
}

