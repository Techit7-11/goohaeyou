package com.ll.gooHaeYu.domain.member.review.controller;

import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostForm;
import com.ll.gooHaeYu.domain.member.review.dto.ReviewDto;
import com.ll.gooHaeYu.domain.member.review.entity.Review;
import com.ll.gooHaeYu.domain.member.review.service.ReviewService;
import com.ll.gooHaeYu.global.rsData.RsData;
import com.ll.gooHaeYu.global.security.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Review", description = "리뷰 API")
@RequiredArgsConstructor
@RequestMapping("/api/member/review")
@RestController
public class ReviewController {

    private ReviewService reviewService;

    @PostMapping
    @Operation(summary = "리뷰 작성")
    public RsData<ReviewDto> createReview(@AuthenticationPrincipal MemberDetails memberDetails,
                                          @Valid @RequestBody ReviewDto reviewDto) {
        ReviewDto savedReview = reviewService.saveReview(reviewDto);
        return RsData.of(savedReview);
    }

    @GetMapping
    @Operation(summary = "리뷰 목록 가져오기")
    public RsData<List<ReviewDto>> getAllReviews() {
        List<ReviewDto> reviews = reviewService.findAllReviews();
        return RsData.of(reviews);
    }
}
