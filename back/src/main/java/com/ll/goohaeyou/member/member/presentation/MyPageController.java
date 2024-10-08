package com.ll.goohaeyou.member.member.presentation;

import com.ll.goohaeyou.auth.domain.MemberDetails;
import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import com.ll.goohaeyou.global.standard.base.Empty;
import com.ll.goohaeyou.jobApplication.application.JobApplicationService;
import com.ll.goohaeyou.jobApplication.application.dto.MyJobApplicationResponse;
import com.ll.goohaeyou.jobPost.comment.application.CommentService;
import com.ll.goohaeyou.jobPost.comment.application.dto.MyCommentResponse;
import com.ll.goohaeyou.jobPost.jobPost.application.JobPostService;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.InterestedJobPostResponse;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.MyPostResponse;
import com.ll.goohaeyou.member.member.application.MemberService;
import com.ll.goohaeyou.member.member.application.dto.MemberResponse;
import com.ll.goohaeyou.member.member.application.dto.ModifyMemberRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "MyPage", description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MyPageController {
    private final MemberService memberService;
    private final JobPostService jobPostService;
    private final JobApplicationService jobApplicationService;
    private final CommentService commentService;

    @GetMapping
    @Operation(summary = "내 정보 조회")
    public ApiResponse<MemberResponse> detailMember(@AuthenticationPrincipal MemberDetails memberDetails) {
        return ApiResponse.ok(memberService.findByUsername(memberDetails.getUsername()));
    }

    @PutMapping
    @Operation(summary = "내 정보 수정")
    public ApiResponse<Empty> modifyMember(@AuthenticationPrincipal MemberDetails memberDetails,
                                           @Valid @RequestBody ModifyMemberRequest request) {
        memberService.modifyMember(memberDetails.getUsername(), request);

        return ApiResponse.noContent();
    }

    @GetMapping("/myposts")
    @Operation(summary = "내 공고 조회")
    public ApiResponse<List<MyPostResponse>> detailMyPosts(@AuthenticationPrincipal MemberDetails memberDetails) {
        return ApiResponse.ok(jobPostService.getMyJobPosts(memberDetails.getUsername()));
    }


    @GetMapping("/myapplications")
    @Operation(summary = "내 지원서 조회")
    public ApiResponse<List<MyJobApplicationResponse>> detailMyApplications(@AuthenticationPrincipal MemberDetails memberDetails) {
        return ApiResponse.ok(jobApplicationService.findByUsername(memberDetails.getUsername()));
    }

    @GetMapping("/mycomments")
    @Operation(summary = "내 댓글 조회")
    public ApiResponse<List<MyCommentResponse>> detailMyComments(@AuthenticationPrincipal MemberDetails memberDetails) {
        return ApiResponse.ok(commentService.findByUsername(memberDetails.getUsername()));
    }

    @GetMapping("/myinterest")
    @Operation(summary = "내 관심 공고 조회")
    public ApiResponse<List<InterestedJobPostResponse>> detailMyInterestingPosts(@AuthenticationPrincipal MemberDetails memberDetails) {
        return ApiResponse.ok(jobPostService.findMyInterestedPosts(memberDetails.getId()));
    }
}
