package com.ll.goohaeyou.auth.application;

import com.ll.goohaeyou.auth.domain.RefreshToken;
import com.ll.goohaeyou.auth.domain.RefreshTokenRepository;
import com.ll.goohaeyou.member.member.application.MemberService;
import com.ll.goohaeyou.member.member.dto.MemberDto;
import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.global.standard.base.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(1);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(1);

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberService memberService;

    public MemberDto authenticateAndSetTokens(String username, HttpServletRequest request, HttpServletResponse response) {
        Member member = memberService.getMember(username);

        // 리프레쉬 토큰
        String refreshToken = jwtTokenProvider.generateToken(member, REFRESH_TOKEN_DURATION);
        saveRefreshToken(member.getId(), refreshToken);
        addTokenToCookie(request, response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, REFRESH_TOKEN_DURATION);

        // 액세스 토큰
        String accessToken = jwtTokenProvider.generateToken(member, ACCESS_TOKEN_DURATION);
        addTokenToCookie(request, response, ACCESS_TOKEN_COOKIE_NAME, accessToken, ACCESS_TOKEN_DURATION);

        return MemberDto.from(member);
    }

    // 리프레쉬 토큰을 DB에 저장
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    // 토큰을 쿠키에 저장
    private void addTokenToCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String token, Duration duration) {
        int cookieMaxAge = (int) duration.toSeconds();
        CookieUtil.deleteCookie(request, response, cookieName);
        CookieUtil.addCookie(response, cookieName, token, cookieMaxAge);
    }
}
