package com.ll.gooHaeYu.domain.member.review.controller;

import com.ll.gooHaeYu.domain.member.review.entity.Review;
import com.ll.gooHaeYu.domain.member.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        Review createdReview = reviewService.createReview(review);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @GetMapping("/jobAds/{jobAdId}")
    public ResponseEntity<List<Review>> getReviewsByJobAdId(@PathVariable Long jobAdId) {
        List<Review> reviews = reviewService.getReviewsByJobAdId(jobAdId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }
}
