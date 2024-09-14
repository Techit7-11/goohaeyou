package com.ll.goohaeyou.review.presentation;

import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import com.ll.goohaeyou.review.application.ReviewService;
import com.ll.goohaeyou.review.application.dto.ApplicantReviewDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member/review")
@RequiredArgsConstructor
@Tag(name = "Review", description = "리뷰 API")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/{jobPostingId}")
    @Operation(summary = "지원자 리뷰 작성")
    public ApiResponse<ApplicantReviewDto> createReview(@PathVariable Long jobPostingId,
                                                        @Valid @RequestBody ApplicantReviewDto applicantReviewDto) {
        applicantReviewDto.setJobPostingId(jobPostingId);
        ApplicantReviewDto savedReview = reviewService.saveReview(applicantReviewDto);
        return ApiResponse.ok(savedReview);
    }

    @GetMapping
    @Operation(summary = "나의 전체 리뷰 조회")
    public ApiResponse<List<ApplicantReviewDto>> getAllReviews() {
        List<ApplicantReviewDto> reviews = reviewService.findReviewsByCurrentUser();
        return ApiResponse.ok(reviews);
    }

    @GetMapping("/{id}")
    @Operation(summary = "리뷰 단건 조회")
    public ApiResponse<ApplicantReviewDto> getReviewById(@PathVariable Long id) {
            ApplicantReviewDto applicantReviewDto = reviewService.findReviewById(id);
            return ApiResponse.ok(applicantReviewDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "리뷰 삭제")
    public ApiResponse<String> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ApiResponse.ok("리뷰가 삭제되었습니다.");
    }
}
