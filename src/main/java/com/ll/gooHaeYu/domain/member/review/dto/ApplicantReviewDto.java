package com.ll.gooHaeYu.domain.member.review.dto;

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
