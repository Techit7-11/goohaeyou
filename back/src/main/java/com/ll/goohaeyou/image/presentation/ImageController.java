package com.ll.goohaeyou.image.presentation;

import com.ll.goohaeyou.image.application.JobPostImageService;
import com.ll.goohaeyou.image.application.ProfileImageService;
import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import com.ll.goohaeyou.auth.domain.MemberDetails;
import com.ll.goohaeyou.global.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Image", description = "이미지 관련 API")
public class ImageController {
    private final ProfileImageService profileImageService;
    private final JobPostImageService jobPostImageService;

    @PutMapping(value = "/api/members/image")
    @Operation(summary = "프로필 이미지 변경")
    public ApiResponse<Empty> updateMemberImage(@AuthenticationPrincipal MemberDetails memberDetails,
                                                @RequestParam(value = "profileImageFile", required = false) MultipartFile profileImageFile) {
        profileImageService.uploadProfileImage(memberDetails.getUsername(), profileImageFile);

        return ApiResponse.noContent();
    }

    @GetMapping(value = "/api/members/image/{username}")
    @Operation(summary = "아이디로 프로필 이미지 URL 불러오기")
    public ApiResponse<String> getMemberImageByUsername(@PathVariable(name = "username") String username) {

        return ApiResponse.ok(profileImageService.getMemberImageByUsername(username));
    }

    @GetMapping(value = "/api/job-posts/{postId}/members/image")
    @Operation(summary = "공고번호로 작성자의 프로필 이미지 URL 불러오기")
    public ApiResponse<String> getMemberImageByPostId(@PathVariable(name = "postId") Long postId) {

        return ApiResponse.ok(profileImageService.getMemberImageByPostId(postId));
    }

    @DeleteMapping(value = "/api/members/image")
    @Operation(summary = "프로필 이미지 삭제")
    public ApiResponse<Empty> deleteMemberImage(@AuthenticationPrincipal MemberDetails memberDetail) {
        profileImageService.deleteProfileImage(memberDetail.getUsername());

        return ApiResponse.noContent();
    }

    @PostMapping(value = "/api/job-post/images")
    @Operation(summary = "공고에 이미지 등록")
    public ApiResponse<Empty> registerPostImages(@AuthenticationPrincipal MemberDetails memberDetails,
                                                Long postDetailId,
                                                @RequestParam(value = "jobPostImageFile", required = false) MultipartFile[] jobPostImageFiles) {
        jobPostImageService.uploadJobPostImages(memberDetails.getUsername(), postDetailId, jobPostImageFiles);

        return ApiResponse.created();
    }

    @GetMapping(value = "/api/job-posts/{postId}/images")
    @Operation(summary = "공고에 등록된 이미지 불러오기")
    public ApiResponse<List<String>> getPostImages(@PathVariable(name = "postId") Long postId) {
        List<String> jobPostImages = jobPostImageService.getJobPostImages(postId);

        return ApiResponse.ok(jobPostImages);
    }

    @PatchMapping(value = "/api/job-post/{postId}/main-image")
    @Operation(summary = "공고의 대표 이미지 변경")
    public ApiResponse<Empty> changeMainImage(@AuthenticationPrincipal MemberDetails memberDetails,
                                              @PathVariable(name = "postId") Long postId,
                                              Long currentImageId, Long newImageId) {
        jobPostImageService.changeMainImage(memberDetails.getUsername(), postId, currentImageId, newImageId);

        return ApiResponse.noContent();
    }

    @DeleteMapping(value = "/api/job-post/{postId}/images")
    @Operation(summary = "공고에 등록된 이미지 삭제")
    public ApiResponse<Empty> deletePostImages(@AuthenticationPrincipal MemberDetails memberDetails,
                                               @PathVariable(name = "postId") Long postId) {
        jobPostImageService.deleteJobPostImages(memberDetails.getUsername(), postId);

        return ApiResponse.noContent();
    }
}
