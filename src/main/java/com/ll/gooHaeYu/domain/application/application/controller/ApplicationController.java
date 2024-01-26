package com.ll.gooHaeYu.domain.application.application.controller;

import com.ll.gooHaeYu.domain.application.application.dto.ApplicationDto;
import com.ll.gooHaeYu.domain.application.application.dto.ApplicationForm;
import com.ll.gooHaeYu.domain.application.application.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping("/{id}")
    public ResponseEntity<String> writeApplication(Authentication authentication,
                                                   @PathVariable(name = "id") Long id,
                                                   @Valid @RequestBody ApplicationForm.Register form) {
        Long ApplicationId = applicationService.writeApplication(authentication.getName(), id, form);

        return ResponseEntity.created(URI.create("/api/application/" + ApplicationId)).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDto> detailApplication(@PathVariable(name = "id") Long id) {
        return  ResponseEntity.ok(applicationService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> modifyApplication(Authentication authentication,
                                           @PathVariable(name = "id") Long id,
                                           @Valid @RequestBody ApplicationForm.Modify form) {
        applicationService.modifyApplication(authentication.getName(), id, form);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(Authentication authentication,
                                           @PathVariable(name = "id") Long id) {
        applicationService.deleteApplication(authentication.getName(), id);

        return ResponseEntity.noContent().build();
    }
}
