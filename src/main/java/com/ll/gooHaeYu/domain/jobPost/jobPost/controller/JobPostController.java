package com.ll.gooHaeYu.domain.jobPost.jobPost.controller;

import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.WriteJobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobPost")
public class JobPostController {

    private final JobPostService jobPostService;

    @PostMapping
    public ResponseEntity<String> jopPost(Authentication authentication) {
        return ResponseEntity.ok()
                .body(authentication.getName() + " 글이 작성되었습니다.");
    }

    @PostMapping("/write")
    public ResponseEntity<String> writePost(Authentication authentication, @Valid @RequestBody WriteJobPost request){

        String post = jobPostService.writePost(authentication.getName(),request);
        return ResponseEntity.ok()
                .body(post + "번 공고가 작성 되었습니다.");
    }

    @PutMapping("/modify/{id}")
    public ResponseEntity<String> modifyPost(Authentication authentication,@PathVariable Long id, @RequestBody WriteJobPost request){
        String post = jobPostService.modifyPost(authentication.getName(),id,request);

        return ResponseEntity.ok()
                .body(post+"번 공고가 수정 되었습니다.");
    }
}
