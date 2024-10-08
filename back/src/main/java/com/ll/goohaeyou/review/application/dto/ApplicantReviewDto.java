package com.ll.goohaeyou.review.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicantReviewDto {
    private Long id;
    private String body;
    private double score;
    private Long jobPostingId;
    private Long applicantId;
}
