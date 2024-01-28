package com.ll.gooHaeYu.domain.application.application.controller;

import com.ll.gooHaeYu.domain.application.application.dto.ApplicationDto;
import com.ll.gooHaeYu.domain.application.application.dto.ApplicationForm;
import com.ll.gooHaeYu.domain.application.application.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<String> writeApplication(Authentication authentication,
                                                   @PathVariable(name = "id") Long id,
                                                   @Valid @RequestBody ApplicationForm.Register form) {
        Long ApplicationId = applicationService.writeApplication(authentication.getName(), id, form);

        return ResponseEntity.created(URI.create("/api/application/" + ApplicationId)).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "지원서 상세 내용")
    public ResponseEntity<ApplicationDto> detailApplication(@PathVariable(name = "id") Long id) {
        return  ResponseEntity.ok(applicationService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "지원서 수정")
    public ResponseEntity<Void> modifyApplication(Authentication authentication,
                                           @PathVariable(name = "id") Long id,
                                           @Valid @RequestBody ApplicationForm.Modify form) {
        applicationService.modifyApplication(authentication.getName(), id, form);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "지원서 삭제")
    public ResponseEntity<Void> deleteApplication(Authentication authentication,
                                           @PathVariable(name = "id") Long id) {
        applicationService.deleteApplication(authentication.getName(), id);

        return ResponseEntity.noContent().build();
    }
}
