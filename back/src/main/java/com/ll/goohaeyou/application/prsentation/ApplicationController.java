package com.ll.goohaeyou.application.prsentation;

import com.ll.goohaeyou.application.application.dto.ApplicationDto;
import com.ll.goohaeyou.application.application.dto.ApplicationForm;
import com.ll.goohaeyou.application.application.ApplicationService;
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
@Tag(name = "Application", description = "지원서 API")
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping("/{id}")
    @Operation(summary = "지원서 작성")
    public ApiResponse<Empty> writeApplication(@AuthenticationPrincipal MemberDetails memberDetails,
                                               @PathVariable(name = "id") Long id,
                                               @Valid @RequestBody ApplicationForm.Register form) {
        applicationService.writeApplication(memberDetails.getUsername(), id, form);

        return ApiResponse.created();
    }

    @GetMapping("/{id}")
    @Operation(summary = "지원서 상세 내용")
    public ApiResponse<ApplicationDto> detailApplication(@PathVariable(name = "id") Long id) {

        return ApiResponse.ok(applicationService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "지원서 수정")
    public ApiResponse<Empty> modifyApplication(@AuthenticationPrincipal MemberDetails memberDetails,
                                                @PathVariable(name = "id") Long id,
                                                @Valid @RequestBody ApplicationForm.Modify form) {
        applicationService.modifyApplication(memberDetails.getUsername(), id, form);

        return ApiResponse.noContent();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "지원서 삭제")
    public ApiResponse<Empty> deleteApplication(@AuthenticationPrincipal MemberDetails memberDetails,
                                                @PathVariable(name = "id") Long id) {
        applicationService.deleteApplication(memberDetails.getUsername(), id);

        return ApiResponse.noContent();
    }
}
