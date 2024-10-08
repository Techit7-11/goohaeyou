package com.ll.goohaeyou.jobPost.employ.presentation;

import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import com.ll.goohaeyou.global.standard.base.Empty;
import com.ll.goohaeyou.jobApplication.application.dto.JobPostApplicationResponse;
import com.ll.goohaeyou.jobPost.employ.application.EmployService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employ/{postId}")
@RequiredArgsConstructor
@Tag(name = "Employ", description = "구인 API")
public class EmployController {
    private final EmployService employService;

    @GetMapping
    @Operation(summary = "공고 별 지원리스트")
    public ApiResponse<List<JobPostApplicationResponse>> getJobApplicationsByPost(Authentication authentication,
                                                                                  @PathVariable (name = "postId") Long postId) {
        return ApiResponse.ok(employService.getList(authentication.getName(), postId));
    }

    @PatchMapping("/{applicationIds}")
    @Operation(summary = "지원서 승인")
    public ApiResponse<Empty> approve(Authentication authentication,
                                      @PathVariable (name="postId") Long postId ,
                                      @PathVariable (name ="applicationIds") List<Long> applicationIds) {

        employService.approve(authentication.getName(), postId, applicationIds);

        return ApiResponse.noContent();
    }
}
