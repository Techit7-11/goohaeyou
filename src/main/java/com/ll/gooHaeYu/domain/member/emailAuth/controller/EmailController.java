package com.ll.gooHaeYu.domain.member.emailAuth.controller;


import com.ll.gooHaeYu.domain.member.emailAuth.service.EmailAuthService;
import com.ll.gooHaeYu.global.apiResponse.ApiResponse;
import com.ll.gooHaeYu.global.security.MemberDetails;
import com.ll.gooHaeYu.standard.base.Empty;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {
    private final EmailAuthService emailAuthService;

    @PostMapping("/auth/email")
    public ApiResponse<Empty> sendAuthEmail(@AuthenticationPrincipal MemberDetails memberDetails) {
        emailAuthService.sendEmail(memberDetails.getUsername(), memberDetails.getEmail());

        return ApiResponse.noContent();
    }
}
