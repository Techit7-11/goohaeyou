package com.ll.gooHaeYu.domain.member.review.service;

import com.ll.gooHaeYu.domain.member.review.entity.Review;
import com.ll.gooHaeYu.domain.member.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByJobAdId(Long jobPostingId) {
        return reviewRepository.findById(jobPostingId);
    }
}
