package com.ll.gooHaeYu.domain.jobPost.jobPost.controller;

import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostForm;
import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostResponseDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostDetailResponseDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobPost")
public class JobPostController {

    private final JobPostService jobPostService;

    @PostMapping("/write")
    public ResponseEntity<String> writePost(Authentication authentication, @Valid @RequestBody JobPostForm.Register request) {
        String post = jobPostService.writePost(authentication.getName(), request);

        return ResponseEntity.ok()
                .body(post + "번 공고가 작성 되었습니다.");
    }

    @PutMapping("/modify/{id}")
    public ResponseEntity<String> modifyPost(Authentication authentication, @PathVariable(name = "id") Long id, @Valid @RequestBody JobPostForm.Modify request) {
        jobPostService.modifyPost(authentication.getName(), id, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePost(Authentication authentication, @PathVariable(name = "id") Long id) {
        jobPostService.deletePost(authentication.getName(), id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<JobPostResponseDto>> findAllPost() {
        List<JobPostResponseDto> posts = jobPostService.findAll()
                .stream()
                .map(JobPostResponseDto::new)
                .toList();

        return ResponseEntity.ok()
                .body(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPostDetailResponseDto> showDetailPost(@PathVariable(name = "id") Long id) {
        JobPost post = jobPostService.findById(id);

        return ResponseEntity.ok()
                .body(new JobPostDetailResponseDto(post));
    }
}
