package com.ll.gooHaeYu.domain.jobPost.employ.controller;

import com.ll.gooHaeYu.domain.application.application.dto.ApplicationDto;
import com.ll.gooHaeYu.domain.jobPost.employ.service.EmployService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Employ", description = "구인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employ/{postId}")
public class EmployController {
    private final EmployService employService;

    @GetMapping
    public ResponseEntity<List<ApplicationDto>> getList(Authentication authentication,
                                                        @PathVariable Long postId) {
        return ResponseEntity.ok(employService.getList(authentication.getName(), postId));
    }
}
