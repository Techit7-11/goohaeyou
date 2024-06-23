package com.ll.goohaeyou.domain.member.review.service;

import com.ll.goohaeyou.domain.member.review.dto.ApplicantReviewDto;

import java.util.List;

public interface ReviewService {
    List<ApplicantReviewDto> findReviewsByCurrentUser();
    ApplicantReviewDto findReviewById(Long id);
    ApplicantReviewDto saveReview(ApplicantReviewDto applicantReviewDto);
    void deleteReview(Long id);
}
