package com.ll.gooHaeYu.domain.jobPost.jobPost.controller;

import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostForm;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.global.security.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "JobPost", description = "구인공고 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job-posts")
public class JobPostController {
    private final JobPostService jobPostService;

    @PostMapping
    @Operation(summary = "구인공고 작성")
    public ResponseEntity<String> writePost(@AuthenticationPrincipal MemberDetails memberDetails,
                                            @Valid @RequestBody JobPostForm.Register form) {
        Long jobPostId = jobPostService.writePost(memberDetails.getUsername(), form);

        return ResponseEntity.created(URI.create("/api/job-posts/" + jobPostId)).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "구인공고 수정")
    public ResponseEntity<Void> modifyPost(Authentication authentication,
                                           @PathVariable(name = "id") Long id,
                                           @Valid @RequestBody JobPostForm.Modify form) {
        jobPostService.modifyPost(authentication.getName(), id, form);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "구인공고 글 목록 가져오기")
    public ResponseEntity<List<JobPostDto>> findAllPost() {
        return ResponseEntity.ok(jobPostService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "구인공고 단건 조회")
    public ResponseEntity<JobPostDto> showDetailPost(@PathVariable(name = "id") Long id) {
        return  ResponseEntity.ok(jobPostService.findById(id));
    }

    @PostMapping("/{id}/interest")
    @Operation(summary = "구인공고 관심 등록")
    public ResponseEntity<Void> increase(Authentication authentication,
                                         @PathVariable(name = "id") Long id) {
        jobPostService.Interest(authentication.getName(),id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/interest")
    @Operation(summary = "구인공고 관심 제거")
    public ResponseEntity<Void> disinterest(Authentication authentication,
                                         @PathVariable(name = "id") Long id) {
        jobPostService.disinterest(authentication.getName(),id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "구인공고 삭제")
    public ResponseEntity<Void> deleteJobPost(Authentication authentication,
                                              @PathVariable(name = "id") Long id) {
        jobPostService.deleteJobPost(authentication.getName(), id);

        return ResponseEntity.noContent().build();
    }
}
