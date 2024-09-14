package com.ll.goohaeyou.member.member.presentation;

import com.ll.goohaeyou.global.standard.base.Empty;
import com.ll.goohaeyou.member.member.application.dto.JoinRequest;
import com.ll.goohaeyou.member.member.application.dto.LoginRequest;
import com.ll.goohaeyou.member.member.application.dto.MemberResponse;
import com.ll.goohaeyou.auth.application.AuthenticationService;
import com.ll.goohaeyou.member.member.application.MemberService;
import com.ll.goohaeyou.member.member.application.dto.UpdateSocialProfileRequest;
import com.ll.goohaeyou.notification.application.NotificationService;
import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import com.ll.goohaeyou.auth.domain.MemberDetails;
import com.ll.goohaeyou.global.infra.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    private final AuthenticationService authenticationService;
    private final NotificationService notificationService;

    @PostMapping("/join")
    @Operation(summary = "회원가입")
    public ApiResponse<Empty> join(@RequestBody @Valid JoinRequest request) {
        memberService.join(request);
        return ApiResponse.created();
    }

    @PostMapping ("/login")
    @Operation(summary = "로그인, accessToken, refreshToken 쿠키 생성됨")
    public ApiResponse<MemberResponse> login(@RequestBody @Valid LoginRequest loginRequest,
                                             HttpServletRequest request, HttpServletResponse response) {
        memberService.login(loginRequest);
        MemberResponse memberResponse = authenticationService.authenticateAndSetTokens(loginRequest.username(), request, response);
        return ApiResponse.ok(memberResponse);
    }

    @PostMapping ("/logout")
    @Operation(summary = "로그아웃 처리 및 쿠키 삭제")
    public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal MemberDetails memberDetails,
                                    HttpServletRequest request, HttpServletResponse response) {
        Stream.of("refresh_token", "access_token", "JSESSIONID")
                .forEach(cookieName -> CookieUtil.deleteCookie(request, response, cookieName));

        notificationService.deleteToken(memberDetails.getId());

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "로그아웃 되었습니다.");
        return ResponseEntity.ok(responseMap);
    }

    @PutMapping("/social")
    @Operation(summary = "최초 소셜로그인 - 필수 회원정보 입력")
    public ApiResponse<MemberResponse> updateSocialMember(@AuthenticationPrincipal MemberDetails memberDetails,
                                                          @Valid @RequestBody UpdateSocialProfileRequest request) {
        MemberResponse updatedMember = memberService.updateSocialMemberProfile(memberDetails.getUsername(), request);

        return ApiResponse.ok(updatedMember);
    }
}