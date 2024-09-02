package com.ll.goohaeyou.jobApplication.prsentation;

import com.ll.goohaeyou.jobApplication.application.dto.JobApplicationDto;
import com.ll.goohaeyou.jobApplication.application.dto.JobApplicationForm;
import com.ll.goohaeyou.jobApplication.application.JobApplicationService;
import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import com.ll.goohaeyou.auth.domain.MemberDetails;
import com.ll.goohaeyou.global.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "JobApplication", description = "지원서 API")
public class JobApplicationController {
    private final JobApplicationService jobApplicationService;

    @PostMapping("/{id}")
    @Operation(summary = "지원서 작성")
    public ApiResponse<Empty> writeJobApplication(@AuthenticationPrincipal MemberDetails memberDetails,
                                               @PathVariable(name = "id") Long id,
                                               @Valid @RequestBody JobApplicationForm.Register form) {
        jobApplicationService.writeApplication(memberDetails.getUsername(), id, form);

        return ApiResponse.created();
    }

    @GetMapping("/{id}")
    @Operation(summary = "지원서 상세 내용")
    public ApiResponse<JobApplicationDto> detailJobApplication(@PathVariable(name = "id") Long id) {

        return ApiResponse.ok(jobApplicationService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "지원서 수정")
    public ApiResponse<Empty> modifyJobApplication(@AuthenticationPrincipal MemberDetails memberDetails,
                                                @PathVariable(name = "id") Long id,
                                                @Valid @RequestBody JobApplicationForm.Modify form) {
        jobApplicationService.modifyJobApplication(memberDetails.getUsername(), id, form);

        return ApiResponse.noContent();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "지원서 삭제")
    public ApiResponse<Empty> deleteJobApplication(@AuthenticationPrincipal MemberDetails memberDetails,
                                                @PathVariable(name = "id") Long id) {
        jobApplicationService.deleteJobApplication(memberDetails.getUsername(), id);

        return ApiResponse.noContent();
    }
}
