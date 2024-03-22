package com.ll.gooHaeYu.domain.jobPost.jobPost.controller;

import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.global.rsData.ApiResponse;
import com.ll.gooHaeYu.global.security.MemberDetails;
import com.ll.gooHaeYu.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Interest", description = "관심 공고 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job-posts")
public class InterestController {

    private final JobPostService jobPostService;

    @PostMapping("/{id}/interest")
    @Operation(summary = "구인공고 관심 등록")
    public ApiResponse<Empty> interest(@AuthenticationPrincipal MemberDetails memberDetails,
                                       @PathVariable(name = "id") Long id) {
        jobPostService.interest(memberDetails.getUsername(), id);

        return ApiResponse.noContent();
    }

    @DeleteMapping("/{id}/interest")
    @Operation(summary = "구인공고 관심 제거")
    public ApiResponse<Empty> disinterest(@AuthenticationPrincipal MemberDetails memberDetails,
                                          @PathVariable(name = "id") Long id) {
        jobPostService.disinterest(memberDetails.getUsername(), id);

        return ApiResponse.noContent();
    }

    @GetMapping("/{id}/members/interest")
    @Operation(summary = "로그인한 유저의 해당 구인공고 관심 등록 여부")
    public ApiResponse<Boolean> isInterested(@AuthenticationPrincipal MemberDetails memberDetails,
                                             @PathVariable(name = "id") Long id) {
        boolean result = jobPostService.isInterested(memberDetails.getUsername(), id);

        return ApiResponse.ok(result);
    }
}
