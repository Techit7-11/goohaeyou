package com.ll.goohaeyou.auth.infrastructure;

import com.ll.goohaeyou.auth.application.CustomUserDetailsService;
import com.ll.goohaeyou.auth.application.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 쿠키에서 액세스 토큰 추출
        String accessToken = extractTokenFromCookies(request, "access_token");

        if (accessToken != null && !jwtTokenProvider.isExpired(accessToken)) { // 토큰 만료 확인 로직 수정
            // 토큰에서 사용자 이름 추출
            String username = jwtTokenProvider.getUsername(accessToken);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);   // 여기서 익셉션 잡기
                if (userDetails != null) {
                    // 권한 부여 및 인증된 사용자의 정보를 SecurityContext에 설정
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } else {
            // 토큰이 만료되었거나 유효하지 않은 경우의 처리 로직
            // 예: 로그 출력, 에러 응답 설정 등
//            logger.info("Invalid or expired JWT token.");
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookies(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            Optional<Cookie> accessTokenCookie = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookieName.equals(cookie.getName()))
                    .findFirst();

            return accessTokenCookie.map(Cookie::getValue).orElse(null);
        }
        return null;
    }
}