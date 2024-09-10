package com.ll.goohaeyou.jobApplication.prsentation;

import com.ll.goohaeyou.auth.domain.MemberDetails;
import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import com.ll.goohaeyou.global.standard.base.Empty;
import com.ll.goohaeyou.jobApplication.application.JobApplicationService;
import com.ll.goohaeyou.jobApplication.application.dto.JobApplicationDetailResponse;
import com.ll.goohaeyou.jobApplication.application.dto.ModifyJobApplicationRequest;
import com.ll.goohaeyou.jobApplication.application.dto.WriteJobApplicationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "JobApplication", description = "지원서 API")
public class JobApplicationController {
    private final JobApplicationService jobApplicationService;

    @PostMapping("/api/job-posts/{postId}/applications")
    @Operation(summary = "지원서 작성")
    public ApiResponse<Empty> writeJobApplication(@AuthenticationPrincipal MemberDetails memberDetails,
                                                  @PathVariable(name = "postId") Long jobPostId,
                                                  @Valid @RequestBody WriteJobApplicationRequest request) {
        jobApplicationService.writeApplication(memberDetails.getUsername(), jobPostId, request);

        return ApiResponse.created();
    }

    @GetMapping("/api/applications/{id}")
    @Operation(summary = "지원서 상세 내용")
    public ApiResponse<JobApplicationDetailResponse> detailJobApplication(@PathVariable(name = "id") Long id) {

        return ApiResponse.ok(jobApplicationService.getDetailById(id));
    }

    @PutMapping("/api/applications/{id}")
    @Operation(summary = "지원서 수정")
    public ApiResponse<Empty> modifyJobApplication(@AuthenticationPrincipal MemberDetails memberDetails,
                                                   @PathVariable(name = "id") Long id,
                                                   @Valid @RequestBody ModifyJobApplicationRequest request) {
        jobApplicationService.modifyJobApplication(memberDetails.getUsername(), id, request);

        return ApiResponse.noContent();
    }

    @DeleteMapping("/api/applications/{id}")
    @Operation(summary = "지원서 삭제")
    public ApiResponse<Empty> deleteJobApplication(@AuthenticationPrincipal MemberDetails memberDetails,
                                                   @PathVariable(name = "id") Long id) {
        jobApplicationService.deleteJobApplication(memberDetails.getUsername(), id);

        return ApiResponse.noContent();
    }
}
