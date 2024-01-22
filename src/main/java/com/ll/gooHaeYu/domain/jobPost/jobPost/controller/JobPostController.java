package com.ll.gooHaeYu.domain.jobPost.jobPost.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jopPost")
public class JobPostController {

    @PostMapping
    public ResponseEntity<String> jopPost(Authentication authentication) {
        return ResponseEntity.ok()
                .body(authentication.getName() + " 글이 작성되었습니다.");
    }
}
