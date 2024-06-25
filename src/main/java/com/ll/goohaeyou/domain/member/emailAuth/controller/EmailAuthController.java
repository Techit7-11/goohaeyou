package com.ll.goohaeyou.domain.member.emailAuth.controller;


import com.ll.goohaeyou.domain.member.emailAuth.service.EmailAuthService;
import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import com.ll.goohaeyou.global.security.MemberDetails;
import com.ll.goohaeyou.global.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "EmailAuth", description = "이메일 인증 API")
@RestController
@RequiredArgsConstructor
public class EmailAuthController {
    private final EmailAuthService emailAuthService;

    @PostMapping("/api/auth/email")
    @Operation(summary = "인증 메일 전송")
    public ApiResponse<Empty> sendAuthEmail(@AuthenticationPrincipal MemberDetails memberDetails) {
        emailAuthService.sendEmail(memberDetails.getUsername(), memberDetails.getEmail());

        return ApiResponse.noContent();
    }

    @PatchMapping("/api/members/verify/{code}")
    @Operation(summary = "인증코드 확인")
    public ApiResponse<Empty> verifyCode(@AuthenticationPrincipal MemberDetails memberDetails,
                                         @PathVariable("code") String code) {
        emailAuthService.confirmVerification(memberDetails.getUsername(), code);

        return ApiResponse.noContent();
    }
}
