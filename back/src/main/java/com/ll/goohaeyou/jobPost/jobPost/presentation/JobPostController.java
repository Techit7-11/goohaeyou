package com.ll.goohaeyou.jobPost.jobPost.presentation;

import com.ll.goohaeyou.jobPost.jobPost.application.dto.JobPostDetailDto;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.JobPostDto;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.JobPostForm;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.application.JobPostService;
import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import com.ll.goohaeyou.global.config.AppConfig;
import com.ll.goohaeyou.auth.domain.MemberDetails;
import com.ll.goohaeyou.global.standard.base.Empty;
import com.ll.goohaeyou.global.standard.base.util.CookieUtil;
import com.ll.goohaeyou.global.standard.dto.PageDto;
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
import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/job-posts")
@RequiredArgsConstructor
@Tag(name = "JobPost", description = "구인공고 API")
public class JobPostController {
    private final JobPostService jobPostService;

    @PostMapping
    @Operation(summary = "구인공고 작성")
    public ApiResponse<Empty> writePost(@AuthenticationPrincipal MemberDetails memberDetails,
                                                       @Valid @RequestBody JobPostForm.Register form) {
        jobPostService.writePost(memberDetails.getUsername(), form);

        return ApiResponse.created();
    }
    @PutMapping("/{id}")
    @Operation(summary = "구인공고 수정")
    public ApiResponse<Empty> modifyPost(@AuthenticationPrincipal MemberDetails memberDetails,
                                                      @PathVariable(name = "id") Long id,
                                                      @Valid @RequestBody JobPostForm.Modify form) {

        jobPostService.modifyPost(memberDetails.getUsername(), id, form);

        return ApiResponse.noContent();
    }

    @GetMapping
    @Operation(summary = "구인공고 글 목록 가져오기")
    public ApiResponse<List<JobPostDto>> findAllPost() {
        return ApiResponse.ok(jobPostService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "구인공고 단건 조회")
    public ApiResponse<JobPostDetailDto> showDetailPost(@PathVariable(name = "id") Long id,
                                                        HttpServletRequest request, HttpServletResponse response) {
        final String VIEWED_JOB_POSTS_COOKIE = "viewedJobPosts";
        boolean isJobPostAlreadyVisited = checkJobPostVisited(request, id, VIEWED_JOB_POSTS_COOKIE);

        // 쿠키 없으면 조회수 증가하고 쿠키 생성
        if (!isJobPostAlreadyVisited) {
            jobPostService.increaseViewCount(id);
            addOrUpdateViewedJobPostsCookie(request, response, id, VIEWED_JOB_POSTS_COOKIE);
        }

        return ApiResponse.ok(jobPostService.findById(id));
    }

    // 방문 여부 확인 (쿠키를 활용)
    private boolean checkJobPostVisited(HttpServletRequest request, Long jobId, String cookieName) {
        Cookie viewCookie = CookieUtil.findCookie(request, cookieName);
        return viewCookie != null && viewCookie.getValue().contains("_" + jobId);
    }

    // 쿠키 추가
    private void addOrUpdateViewedJobPostsCookie(HttpServletRequest request, HttpServletResponse response,
                                                 Long jobId, String cookieName) {
        // 쿠키에서 방문한 게시물 ID 목록 가져옴
        Cookie viewCookie = CookieUtil.findCookie(request, cookieName);
        String newCookieValue;

        // 가져온 목록에 현재 게시물 ID 추가
        if (viewCookie != null) {
            String existingValue = viewCookie.getValue();
            if (!existingValue.contains("_" + jobId)) {
                newCookieValue = existingValue + "_" + jobId;
            } else {
                // 이미 방문한 게시물이면 쿠키 값을 변경 X
                return;
            }
        } else {
            newCookieValue = "_" + jobId;
        }

        // 새로운 쿠키 값 설정 or 기존 쿠키 업데이트
        CookieUtil.addCookie(response, cookieName, newCookieValue, 24 * 60 * 60); // 24시간
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "구인공고 삭제")
    public ResponseEntity<Empty> deleteJobPost(@AuthenticationPrincipal MemberDetails memberDetails,
                                              @PathVariable(name = "id") Long id) {
        jobPostService.deleteJobPost(memberDetails.getUsername(), id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "게시물 검색")
    public ApiResponse<List<JobPostDto>> searchJobPostsByTitleAndBody(
            @RequestParam(required = false, name = "titleOrBody") String titleOrBody,
            @RequestParam(required = false, name = "title") String title,
            @RequestParam(required = false, name = "body") String body) {

        return ApiResponse.ok(jobPostService.searchJobPostsByTitleAndBody(titleOrBody, title, body));
    }


    @GetMapping("/search-sort")
    @Operation(summary = "구인공고 검색")
    public ApiResponse<GetPostsResponseBody> postSearchAndSort(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String kw,
            @RequestParam(value = "kwType", defaultValue = "") List<String> kwTypes,
            @RequestParam(defaultValue = "") String closed,
            @RequestParam(defaultValue = "") String gender,
            @RequestParam(defaultValue = "0") int[] min_Age,
            @RequestParam(defaultValue = "") List<String> location
            ) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));

        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));

        Page<JobPost> itemPage = jobPostService.findByKw(kwTypes, kw, closed, gender, min_Age, location, pageable);

        Page<JobPostDto> _itemPage = JobPostDto.convertToDtoPage(itemPage);

        return ApiResponse.ok(
                new GetPostsResponseBody(
                        new PageDto<>(_itemPage)
                )
        );
    }

    public record GetPostsResponseBody(@NonNull PageDto<JobPostDto> itemPage) {
    }

    @GetMapping("/sort")
    @Operation(summary = "구인공고 글 목록 정렬")
    public ApiResponse<GetPostsResponseBody> findAllPostSort(
            @RequestParam(value = "page", defaultValue = "1") int page,
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

        Page<JobPost> itemPage = jobPostService.findBySort(pageable);
        Page<JobPostDto> _itemPage = JobPostDto.convertToDtoPage(itemPage);

        return ApiResponse.ok(
                new GetPostsResponseBody(
                        new PageDto<>(_itemPage)
                )
        );
    }

    @PutMapping("/{id}/closing")
    @Operation(summary = "조기 마감")
    public ApiResponse<Empty> postEarlyClosing(@AuthenticationPrincipal MemberDetails memberDetails,
                                               @PathVariable(name = "id") Long id) {
        jobPostService.closeJobPostEarly(memberDetails.getUsername(), id);

        return ApiResponse.noContent();
    }

    @GetMapping("/by-category")
    @Operation(summary = "카테고리의 글 목록 불러오기")
    public ApiResponse<Page<JobPostDto>> getPostsByCategory(@RequestParam("category-name") String categoryName,
                                                            @RequestParam(value = "page", defaultValue = "1") int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);

        Page<JobPostDto> jobPostDtoPage = jobPostService.getPostsByCategory(categoryName, pageable);

        return ApiResponse.ok(jobPostDtoPage);
    }
}
