package com.ll.gooHaeYu.domain.member.review.service;

import com.ll.gooHaeYu.domain.mapper.ReviewMapper;
import com.ll.gooHaeYu.domain.member.review.dto.ReviewDto;
import com.ll.gooHaeYu.domain.member.review.entity.Review;
import com.ll.gooHaeYu.domain.member.review.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final ReviewRepository reviewRepository;

    @Override
    public ReviewDto saveReview(ReviewDto reviewDto) {
        Review review = reviewMapper.toEntity(reviewDto);
        review = reviewRepository.save(review);
        return reviewMapper.toDto(review);
    }

    @Override
    public List<ReviewDto> findAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDto findReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + id));
        return reviewMapper.toDto(review);
    }


    @Override
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
