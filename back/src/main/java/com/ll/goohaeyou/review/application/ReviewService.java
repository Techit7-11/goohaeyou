package com.ll.goohaeyou.review.application;

import com.ll.goohaeyou.review.application.dto.ApplicantReviewDto;

import java.util.List;

public interface ReviewService {
    List<ApplicantReviewDto> findReviewsByCurrentUser();
    ApplicantReviewDto findReviewById(Long id);
    ApplicantReviewDto saveReview(ApplicantReviewDto applicantReviewDto);
    void deleteReview(Long id);
}
