package com.ll.gooHaeYu.domain.fileupload.controller;

import com.ll.gooHaeYu.domain.fileupload.service.ProfileImageService;
import com.ll.gooHaeYu.global.apiResponse.ApiResponse;
import com.ll.gooHaeYu.global.security.MemberDetails;
import com.ll.gooHaeYu.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "S3-Image", description = "이미지 업로드 관련 API")
@RestController
@RequiredArgsConstructor
public class S3ImageController {
    private final ProfileImageService profileImageService;

    @PutMapping(value = "/api/members/image")
    @Operation(summary = "프로필 이미지 변경")
    public ApiResponse<Empty> updateMemberImage(@AuthenticationPrincipal MemberDetails memberDetails,
                                                @RequestPart(value = "profileImageFile", required = false) MultipartFile profileImageFile) {
        profileImageService.uploadProfileImage(memberDetails.getUsername(), profileImageFile);

        return ApiResponse.noContent();
    }

    @GetMapping(value = "/api/members/image/{username}")
    @Operation(summary = "아이디로 프로필 이미지 URL 불러오기")
    public ApiResponse<String> getMemberImageByUsername(@PathVariable(name = "username") String username) {

        return ApiResponse.ok(profileImageService.getMemberImageByUsername(username));
    }

    @GetMapping(value = "/api/members/image/posts/{postId}")
    @Operation(summary = "공고번호로 작성자의 프로필 이미지 URL 불러오기")
    public ApiResponse<String> getMemberImageByPostId(@PathVariable(name = "postId") Long postId) {

        return ApiResponse.ok(profileImageService.getMemberImageByPostId(postId));
    }
}
