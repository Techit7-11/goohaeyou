package com.ll.gooHaeYu.domain.fileupload.controller;

import com.ll.gooHaeYu.domain.fileupload.service.ProfileImageService;
import com.ll.gooHaeYu.global.apiResponse.ApiResponse;
import com.ll.gooHaeYu.global.security.MemberDetails;
import com.ll.gooHaeYu.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping(value = "/api/members/image")
    @Operation(summary = "프로필 이미지 URL 불러오기")
    public ApiResponse<String> getMemberImage(@AuthenticationPrincipal MemberDetails memberDetails) {

        return ApiResponse.ok(profileImageService.getProfileImage(memberDetails.getUsername()));
    }
}
