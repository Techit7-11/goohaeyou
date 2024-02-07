package com.ll.gooHaeYu.global.security.OAuth;

import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.entity.RefreshToken;
import com.ll.gooHaeYu.domain.member.member.repository.RefreshTokenRepository;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import com.ll.gooHaeYu.global.config.AppConfig;
import com.ll.gooHaeYu.global.security.JwtTokenProvider;
import com.ll.gooHaeYu.standard.base.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(1);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(1);
    public static final String REDIRECT_PATH = AppConfig.getSiteFrontUrl();

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Member member = memberService.getMember((String) oAuth2User.getAttributes().get("email"));

        String refreshToken = jwtTokenProvider.generateToken(member, REFRESH_TOKEN_DURATION);
        saveRefreshToken(member.getId(), refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        String accessToken = jwtTokenProvider.generateToken(member, ACCESS_TOKEN_DURATION);
        addAccessTokenToCookie(request, response, accessToken);

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, REDIRECT_PATH);
    }

    // 리프레쉬 토큰 DB 저장
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    // 액세스 토큰 쿠키 저장
    private void addAccessTokenToCookie(HttpServletRequest request, HttpServletResponse response, String accessToken) {
        int cookieMaxAge = (int) ACCESS_TOKEN_DURATION.toSeconds();

        CookieUtil.deleteCookie(request, response, ACCESS_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken, cookieMaxAge);
    }

    // 리프레쉬 토큰 쿠키 저장
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    //인증 과정 중 생긴 요청 속성 제거
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
