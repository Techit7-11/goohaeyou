package com.ll.gooHaeYu.domain.jobPost.jobPost.controller;

import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.GetJobPostResponseDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.GetJobPostDetailResponseDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.WriteJobPostRequestDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobPost")
public class JobPostController {

    private final JobPostService jobPostService;

    @PostMapping("/write")
    public ResponseEntity<String> writePost(Authentication authentication, @Valid @RequestBody WriteJobPostRequestDto request) {
        String post = jobPostService.writePost(authentication.getName(), request);

        return ResponseEntity.ok()
                .body(post + "번 공고가 작성 되었습니다.");
    }

    @PutMapping("/modify/{id}")
    public ResponseEntity<String> modifyPost(Authentication authentication, @PathVariable(name = "id") Long id, @RequestBody WriteJobPostRequestDto request) {
        String post = jobPostService.modifyPost(authentication.getName(), id, request);

        return ResponseEntity.ok()
                .body(post + "번 공고가 수정 되었습니다.");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePost(Authentication authentication, @PathVariable(name = "id") Long id) {
        String post = jobPostService.deletePost(authentication.getName(), id);

        return ResponseEntity.ok()
                .body(post + "번 공고가 삭제 되었습니다.");
    }

    @GetMapping
    public ResponseEntity<List<GetJobPostResponseDto>> findAllPost() {
        List<GetJobPostResponseDto> posts = jobPostService.findAll()
                .stream()
                .map(GetJobPostResponseDto::new)
                .toList();

        return ResponseEntity.ok()
                .body(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetJobPostDetailResponseDto> showDetailPost(@PathVariable(name = "id") Long id) {
        JobPost post = jobPostService.findById(id);

        return ResponseEntity.ok()
                .body(new GetJobPostDetailResponseDto(post));
    }
}
