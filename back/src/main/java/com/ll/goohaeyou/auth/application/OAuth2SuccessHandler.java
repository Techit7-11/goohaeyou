package com.ll.goohaeyou.auth.application;

import com.ll.goohaeyou.auth.domain.RefreshTokenDomainService;
import com.ll.goohaeyou.auth.infrastructure.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.ll.goohaeyou.global.infra.util.CookieUtil;
import com.ll.goohaeyou.member.member.domain.MemberDomainService;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(1);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(1);

    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final RefreshTokenDomainService refreshTokenDomainService;
    private final MemberDomainService memberDomainService;

    @Value("${custom.site.frontUrl}/member/socialLoginCallback")
    private String redirectPath;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Member member = memberDomainService.getByUsername((String) oAuth2User.getAttributes().get("email"));

        // 리프레쉬 토큰
        String refreshToken = jwtTokenProvider.generateToken(member, REFRESH_TOKEN_DURATION);
        refreshTokenDomainService.save(member.getId(), refreshToken);
        addTokenToCookie(request, response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, REFRESH_TOKEN_DURATION);

        // 액세스 토큰
        String accessToken = jwtTokenProvider.generateToken(member, ACCESS_TOKEN_DURATION);
        addTokenToCookie(request, response, ACCESS_TOKEN_COOKIE_NAME, accessToken, ACCESS_TOKEN_DURATION);

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, redirectPath);
    }

    // 토큰 쿠키 저장
    private void addTokenToCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String token, Duration duration) {
        int cookieMaxAge = (int) duration.toSeconds();
        CookieUtil.deleteCookie(request, response, cookieName);
        CookieUtil.addCookie(response, cookieName, token, cookieMaxAge);
    }

    //인증 과정 중 생긴 요청 속성 제거
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
