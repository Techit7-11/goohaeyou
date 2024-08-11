package com.ll.goohaeyou.jobPost.jobPost.controller;

import com.ll.goohaeyou.jobPost.jobPost.service.InterestService;
import com.ll.goohaeyou.jobPost.jobPost.service.JobPostService;
import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import com.ll.goohaeyou.global.security.MemberDetails;
import com.ll.goohaeyou.global.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job-posts")
@RequiredArgsConstructor
@Tag(name = "Interest", description = "관심 공고 API")
public class InterestController {
    private final JobPostService jobPostService;
    private final InterestService interestService;

    @PostMapping("/{id}/interest")
    @Operation(summary = "구인공고 관심 등록")
    public ApiResponse<Empty> interest(@AuthenticationPrincipal MemberDetails memberDetails,
                                       @PathVariable(name = "id") Long id) {
        interestService.interest(memberDetails.getUsername(), id);

        return ApiResponse.noContent();
    }

    @DeleteMapping("/{id}/interest")
    @Operation(summary = "구인공고 관심 제거")
    public ApiResponse<Empty> disinterest(@AuthenticationPrincipal MemberDetails memberDetails,
                                          @PathVariable(name = "id") Long id) {
        interestService.disinterest(memberDetails.getUsername(), id);

        return ApiResponse.noContent();
    }

    @GetMapping("/{id}/members/interest")
    @Operation(summary = "로그인한 유저의 해당 구인공고 관심 등록 여부")
    public ApiResponse<Boolean> isInterested(@AuthenticationPrincipal MemberDetails memberDetails,
                                             @PathVariable(name = "id") Long id) {
        boolean result = interestService.isInterested(memberDetails.getUsername(), id);

        return ApiResponse.ok(result);
    }
}
