package com.ll.gooHaeYu.domain.jobPost.jobPost.controller;

import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostDetailDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostForm;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.global.rsData.RsData;
import com.ll.gooHaeYu.global.security.MemberDetails;
import com.ll.gooHaeYu.standard.base.util.CookieUtil;
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
import java.util.Arrays;
import java.util.List;

@Tag(name = "JobPost", description = "구인공고 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job-posts")
public class JobPostController {
    private final JobPostService jobPostService;

    @PostMapping
    @Operation(summary = "구인공고 작성")
    public RsData<URI> writePost(@AuthenticationPrincipal MemberDetails memberDetails,
                                            @Valid @RequestBody JobPostForm.Register form) {
        Long jobPostId = jobPostService.writePost(memberDetails.getUsername(), form);

        return RsData.of("201", "CREATE", URI.create("/api/job-posts/" + jobPostId));
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
    public RsData<JobPostDetailDto> showDetailPost(@PathVariable(name = "id") Long id, HttpServletRequest request, HttpServletResponse response) {
        final String VIEWED_JOB_POSTS_COOKIE = "viewedJobPosts";
        boolean isJobPostAlreadyVisited = checkJobPostVisited(request, id, VIEWED_JOB_POSTS_COOKIE);

        // 쿠키 없으면 조회수 증가하고 쿠키 생성
        if (!isJobPostAlreadyVisited) {
            jobPostService.increaseViewCount(id);
            addOrUpdateViewedJobPostsCookie(request, response, id, VIEWED_JOB_POSTS_COOKIE);
        }

        return  RsData.of(jobPostService.findById(id));
    }

    // 방문 여부 확인 (쿠키를 활용)
    private boolean checkJobPostVisited(HttpServletRequest request, Long jobId, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName) && cookie.getValue().contains("[" + jobId + "]")) {
                    return true;
                }
            }
        }
        return false;
    }

    // 쿠키 추가
    private void addOrUpdateViewedJobPostsCookie(HttpServletRequest request, HttpServletResponse response, Long jobId, String cookieName) {
        // 쿠키에서 방문한 게시물 ID 목록 가져옴
        Cookie viewCookie = Arrays.stream(request.getCookies() != null ? request.getCookies() : new Cookie[0])
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst()
                .orElse(null);

        // 가져온 목록에 현재 게시물 ID 추가
        String newCookieValue;
        if (viewCookie != null) {
            String existingValue = viewCookie.getValue();
            if (!existingValue.contains("[" + jobId + "]")) {
                newCookieValue = existingValue + "[" + jobId + "]";
            } else {
                // 이미 방문한 게시물이면 쿠키 값 변경 x
                return;
            }
        } else {
            newCookieValue = "[" + jobId + "]";
        }

        // 새로운 쿠키 값 설정 or 기존 쿠키 업데이트
        CookieUtil.addCookie(response, cookieName, newCookieValue, 24 * 60 * 60);
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

    @DeleteMapping("/{id}/deadline")
    @Operation(summary = "공고 마감")
    public ResponseEntity<Void> deadline(@AuthenticationPrincipal MemberDetails memberDetails,
                                         @PathVariable(name = "id") Long id) {
        jobPostService.deadline(memberDetails.getUsername(), id);

        return ResponseEntity.noContent().build();
    }
}
