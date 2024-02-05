package com.ll.gooHaeYu.domain.jobPost.jobPost.controller;

import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostForm;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.global.config.AppConfig;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
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
    public RsData<JobPostDto> showDetailPost(@PathVariable(name = "id") Long id,  HttpServletRequest request, HttpServletResponse response) {
        final String VIEWED_JOB_POSTS_COOKIE = "viewedJobPosts";
        boolean isJobPostAlreadyVisited = checkJobPostVisited(request, id, VIEWED_JOB_POSTS_COOKIE);

        // 쿠키 없으면 조회수 증가하고 쿠키 생성
        if (!isJobPostAlreadyVisited) {
            jobPostService.increaseViewCount(id);
            addOrUpdateViewedJobPostsCookie(response, id, VIEWED_JOB_POSTS_COOKIE);
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
    private void addOrUpdateViewedJobPostsCookie(HttpServletResponse response, Long jobId, String cookieName) {
        String cookieValue = "[" + jobId + "]";
        CookieUtil.addCookie(response, cookieName, cookieValue, 24 * 60 * 60);
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

    @GetMapping("/sort")
    @Operation(summary = "구인공고 글 목록 정렬")
    public ResponseEntity<Page<JobPostDto>> findAllPostSort(
            @RequestParam(value="page", defaultValue="1") int page,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") List<String> sortBys,
            @RequestParam(name = "sortOrder", defaultValue = "desc") List<String> sortOrders
    ) {
        List<Sort.Order> sorts = new ArrayList<>();

        for (int i = 0; i < sortBys.size(); i++) {
            String sortBy = sortBys.get(i);
            String sortOrder = i < sortOrders.size() ? sortOrders.get(i) : "desc"; // 기본값은 desc
            sorts.add(new Sort.Order(Sort.Direction.fromString(sortOrder), sortBy));
        }

        Pageable pageable = PageRequest.of(page - 1, AppConfig.getBasePageSize(), Sort.by(sorts));
        System.out.println(sortOrders);

        Page<JobPost> itemPage = jobPostService.findBySort(pageable);
        Page<JobPostDto> _itemPage = JobPostDto.toDtoListPage(itemPage);

        return ResponseEntity.ok(_itemPage);
    }
}
