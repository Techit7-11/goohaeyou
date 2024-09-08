package com.ll.goohaeyou.jobPost.jobPost.presentation;

import com.ll.goohaeyou.auth.domain.MemberDetails;
import com.ll.goohaeyou.category.application.JobPostCategoryService;
import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import com.ll.goohaeyou.global.standard.base.Empty;
import com.ll.goohaeyou.jobPost.jobPost.application.EssentialService;
import com.ll.goohaeyou.jobPost.jobPost.application.JobPostService;
import com.ll.goohaeyou.jobPost.jobPost.application.WageService;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-posts")
@RequiredArgsConstructor
@Tag(name = "JobPost", description = "구인공고 API")
public class JobPostController {
    private final JobPostService jobPostService;
    private final JobPostCategoryService jobPostCategoryService;
    private final WageService wageService;
    private final EssentialService essentialService;

    @PostMapping
    @Operation(summary = "구인공고 작성")
    public ApiResponse<Empty> writePost(@AuthenticationPrincipal MemberDetails memberDetails,
                                        @Valid @RequestBody WriteJobPostRequest request) {
        Long jobPostId = jobPostService.writePost(memberDetails.getUsername(), request);

        jobPostCategoryService.create(jobPostId, request.location(), request.categoryId());
        wageService.create(jobPostId, request);
        essentialService.create(jobPostId, request.minAge(), request.gender());

        return ApiResponse.created();
    }
    @PutMapping("/{id}")
    @Operation(summary = "구인공고 수정")
    public ApiResponse<Empty> modifyPost(@AuthenticationPrincipal MemberDetails memberDetails,
                                         @PathVariable(name = "id") Long id,
                                         @Valid @RequestBody ModifyJobPostRequest request) {

        jobPostService.modifyPost(memberDetails.getUsername(), id, request);

        return ApiResponse.noContent();
    }

    @GetMapping("/{id}")
    @Operation(summary = "구인공고 단건 조회")
    public ApiResponse<JobPostDetailResponse> showDetailPost(@PathVariable(name = "id") Long postId,
                                                             HttpServletRequest httpRequest, HttpServletResponse HttpResponse) {

        return ApiResponse.ok(jobPostService.getJobPostDetail(postId, httpRequest, HttpResponse));
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
    public ApiResponse<List<JobPostBasicResponse>> searchJobPostsByTitleAndBody(
            @RequestParam(required = false, name = "titleOrBody") String titleOrBody,
            @RequestParam(required = false, name = "title") String title,
            @RequestParam(required = false, name = "body") String body) {

        return ApiResponse.ok(jobPostService.searchJobPostsByTitleAndBody(titleOrBody, title, body));
    }


    @GetMapping("/search-sort")
    @Operation(summary = "구인공고 검색")
    public ApiResponse<Page<JobPostBasicResponse>> postSearchAndSort(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String kw,
            @RequestParam(value = "kwType", defaultValue = "") List<String> kwTypes,
            @RequestParam(defaultValue = "") String closed,
            @RequestParam(defaultValue = "") String gender,
            @RequestParam(defaultValue = "0") int[] min_Age,
            @RequestParam(defaultValue = "") List<String> location
            ) {

        return ApiResponse.ok(jobPostService.findByKw(kwTypes, kw, closed, gender, min_Age, location, page));
    }

    @GetMapping("/sort")
    @Operation(summary = "구인공고 글 목록 정렬")
    public ApiResponse<JobPostSortPageResponse> findAllPostSort(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") List<String> sortBys,
            @RequestParam(name = "sortOrder", defaultValue = "desc") List<String> sortOrders
    ) {
        Pageable pageable = jobPostService.createPageableForSorting(page, sortBys, sortOrders);

        return ApiResponse.ok(jobPostService.findBySort(pageable));
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
    public ApiResponse<Page<JobPostBasicResponse>> getPostsByCategory(@RequestParam("category-name") String categoryName,
                                                            @RequestParam(value = "page", defaultValue = "1") int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);

        return ApiResponse.ok(jobPostService.getPostsByCategory(categoryName, pageable));
    }
}
