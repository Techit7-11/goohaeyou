package com.ll.gooHaeYu.domain.application.application.controller;

import com.ll.gooHaeYu.domain.application.application.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/application")
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping("/{id}")
    public ResponseEntity<String> writeApplication(Authentication authentication, @PathVariable(name = "id") Long id) {
        Long ApplicationId = applicationService.writeApplication(authentication.getName(), id);

        return ResponseEntity.created(URI.create("/api/application/" + ApplicationId)).build();
    }
}
