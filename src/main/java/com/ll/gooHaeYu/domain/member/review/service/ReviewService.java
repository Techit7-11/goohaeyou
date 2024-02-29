package com.ll.gooHaeYu.domain.member.review.service;

import com.ll.gooHaeYu.domain.member.review.dto.ReviewDto;

import java.util.List;

public interface ReviewService {
    List<ReviewDto> findAllReviews();
    ReviewDto findReviewById(Long id);
    ReviewDto saveReview(ReviewDto reviewDto);
    void deleteReview(Long id);
}
