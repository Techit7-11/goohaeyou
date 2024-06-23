package com.ll.goohaeyou.domain.member.member.controller;

import com.ll.goohaeyou.global.config.AppConfig;
import com.ll.goohaeyou.global.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.ll.goohaeyou.global.exception.ErrorCode.INVALID_LOGIN_REQUEST;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class OAuthController {

    @GetMapping("/socialLogin/{providerTypeCode}")
    @Operation(summary = "소셜 로그인")
    public String socialLogin(String redirectUrl, @PathVariable String providerTypeCode) {
        if (!redirectUrl.startsWith(AppConfig.getSiteFrontUrl())) {
            throw new CustomException(INVALID_LOGIN_REQUEST);
        }

        return "redirect:/oauth2/authorization/" + providerTypeCode;
    }

    @GetMapping("/login")
    public String login() {
        return "oauthLogin";
    }
}
