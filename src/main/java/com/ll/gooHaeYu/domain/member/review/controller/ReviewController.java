package com.ll.gooHaeYu.domain.member.review.controller;

import com.ll.gooHaeYu.domain.member.review.dto.ApplicantReviewDto;
import com.ll.gooHaeYu.domain.member.review.service.ReviewService;
import com.ll.gooHaeYu.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Review", description = "리뷰 API")
@RequiredArgsConstructor
@RequestMapping("/api/member/review")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "지원자 리뷰 작성")
    public RsData<ApplicantReviewDto> createReview(@Valid @RequestBody ApplicantReviewDto applicantReviewDto) {
        ApplicantReviewDto savedReview = reviewService.saveReview(applicantReviewDto);
        return RsData.of(savedReview);
    }

    @GetMapping
    @Operation(summary = "나의 전체 리뷰 조회")
    public RsData<List<ApplicantReviewDto>> getAllReviews() {
        List<ApplicantReviewDto> reviews = reviewService.findReviewsByCurrentUser();
        return RsData.of(reviews);
    }

    @GetMapping("/{id}")
    @Operation(summary = "리뷰 단건 조회")
    public RsData<ApplicantReviewDto> getReviewById(@PathVariable Long id) {
            ApplicantReviewDto applicantReviewDto = reviewService.findReviewById(id);
            return RsData.of(applicantReviewDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "리뷰 삭제")
    public RsData<String> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return RsData.of("리뷰가 삭제되었습니다.");
    }
}
