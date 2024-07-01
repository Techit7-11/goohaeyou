package com.ll.gooHaeYu.domain.application.application.controller;

import com.ll.gooHaeYu.domain.application.application.dto.ApplicationDto;
import com.ll.gooHaeYu.domain.application.application.dto.ApplicationForm;
import com.ll.gooHaeYu.domain.application.application.service.ApplicationService;
import com.ll.gooHaeYu.global.rsData.RsData;
import com.ll.gooHaeYu.global.security.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Application", description = "지원서 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping("/{id}")
    @Operation(summary = "지원서 작성")
    public RsData<URI> writeApplication(@AuthenticationPrincipal MemberDetails memberDetails,
                                                   @PathVariable(name = "id") Long id,
                                                   @Valid @RequestBody ApplicationForm.Register form) {
        Long ApplicationId = applicationService.writeApplication(memberDetails.getUsername(), id, form);

        return RsData.of("201", "CREATE", URI.create("/api/application/" + ApplicationId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "지원서 상세 내용")
    public RsData<ApplicationDto> detailApplication(@PathVariable(name = "id") Long id) {
        return  RsData.of(applicationService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "지원서 수정")
    public RsData<Void> modifyApplication(@AuthenticationPrincipal MemberDetails memberDetails,
                                           @PathVariable(name = "id") Long id,
                                           @Valid @RequestBody ApplicationForm.Modify form) {
        applicationService.modifyApplication(memberDetails.getUsername(), id, form);

        return RsData.of("204", "NO_CONTENT");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "지원서 삭제")
    public RsData<Void> deleteApplication(@AuthenticationPrincipal MemberDetails memberDetails,
                                           @PathVariable(name = "id") Long id) {
        applicationService.deleteApplication(memberDetails.getUsername(), id);

        return RsData.of("204", "NO_CONTENT");
    }
}
