package com.ll.gooHaeYu.domain.member.emailAuth.controller;


import com.ll.gooHaeYu.domain.member.emailAuth.service.EmailAuthService;
import com.ll.gooHaeYu.global.apiResponse.ApiResponse;
import com.ll.gooHaeYu.global.security.MemberDetails;
import com.ll.gooHaeYu.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "EmailAuth", description = "이메일 인증 API")
@RestController
@RequiredArgsConstructor
public class EmailAuthController {
    private final EmailAuthService emailAuthService;

    @PostMapping("/auth/email")
    @Operation(summary = "인증 메일 전송")
    public ApiResponse<Empty> sendAuthEmail(@AuthenticationPrincipal MemberDetails memberDetails) {
        emailAuthService.sendEmail(memberDetails.getUsername(), memberDetails.getEmail());

        return ApiResponse.noContent();
    }


}
