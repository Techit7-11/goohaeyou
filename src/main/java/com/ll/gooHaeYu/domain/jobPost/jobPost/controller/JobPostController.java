package com.ll.gooHaeYu.domain.jobPost.jobPost.controller;

import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostForm;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job-posts")
public class JobPostController {
    private final JobPostService jobPostService;

    @PostMapping
    public ResponseEntity<String> writePost(Authentication authentication,
                                            @Valid @RequestBody JobPostForm.Register form) {
        Long jobPostId = jobPostService.writePost(authentication.getName(), form);

        return ResponseEntity.created(URI.create("/api/job-posts/" + jobPostId)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> modifyPost(Authentication authentication,
                                             @PathVariable(name = "id") Long id,
                                             @Valid @RequestBody JobPostForm.Modify form) {
        jobPostService.modifyPost(authentication.getName(), id, form);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(Authentication authentication,
                                            @PathVariable(name = "id") Long id) {
        jobPostService.deletePost(authentication.getName(), id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<JobPostDto>> findAllPost() {
        List<JobPostDto> jobPostDtos = jobPostService.findAll();

        return ResponseEntity.ok()
                .body(jobPostDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPostDto> showDetailPost(@PathVariable(name = "id") Long id) {
        JobPostDto jobPostDto = jobPostService.findById(id);

        return ResponseEntity.ok()
                .body(jobPostDto);
    }
}
