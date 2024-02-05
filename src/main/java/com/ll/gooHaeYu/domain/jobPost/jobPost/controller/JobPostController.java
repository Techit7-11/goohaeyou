package com.ll.gooHaeYu.domain.jobPost.jobPost.controller;

import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostForm;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.global.rsData.RsData;
import com.ll.gooHaeYu.global.security.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> modifyPost(@AuthenticationPrincipal MemberDetails memberDetails,
                                           @PathVariable(name = "id") Long id,
                                           @Valid @RequestBody JobPostForm.Modify form) {
        jobPostService.modifyPost(memberDetails.getUsername(), id, form);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "구인공고 글 목록 가져오기")
    public RsData<List<JobPostDto>> findAllPost() {
        return RsData.of(jobPostService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "구인공고 단건 조회")
    public RsData<JobPostDto> showDetailPost(@PathVariable(name = "id") Long id,  HttpServletRequest request, HttpServletResponse response) {
        viewCountUp(id, request, response);
        return  RsData.of(jobPostService.findById(id));
    }
    private void viewCountUp(Long id, HttpServletRequest request, HttpServletResponse response) {

        Cookie oldCookie = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jobPost")) {
                    oldCookie = cookie;
                }
            }
        }

        if (oldCookie != null) {
            if (!oldCookie.getValue().contains("[" + id.toString() + "]")) {
                jobPostService.increaseViewCount(id);
                oldCookie.setValue(oldCookie.getValue() + "_[" + id + "]");
                oldCookie.setPath("/");
                oldCookie.setMaxAge(60 * 60 * 24);
                response.addCookie(oldCookie);
            }
        } else {
            jobPostService.increaseViewCount(id);
            Cookie newCookie = new Cookie("jobPost","[" + id + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24);
            response.addCookie(newCookie);
        }
    }

    @PostMapping("/{id}/interest")
    @Operation(summary = "구인공고 관심 등록")
    public ResponseEntity<Void> increase(@AuthenticationPrincipal MemberDetails memberDetails,
                                         @PathVariable(name = "id") Long id) {
        jobPostService.Interest(memberDetails.getUsername(),id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/interest")
    @Operation(summary = "구인공고 관심 제거")
    public ResponseEntity<Void> disinterest(@AuthenticationPrincipal MemberDetails memberDetails,
                                         @PathVariable(name = "id") Long id) {
        jobPostService.disinterest(memberDetails.getUsername(),id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "구인공고 삭제")
    public ResponseEntity<Void> deleteJobPost(@AuthenticationPrincipal MemberDetails memberDetails,
                                              @PathVariable(name = "id") Long id) {
        jobPostService.deleteJobPost(memberDetails.getUsername(), id);

        return ResponseEntity.noContent().build();
    }
}
