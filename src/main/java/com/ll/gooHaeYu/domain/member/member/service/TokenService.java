package com.ll.gooHaeYu.domain.member.member.service;

import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.global.exception.CustomException;
import com.ll.gooHaeYu.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.ll.gooHaeYu.global.exception.ErrorCode.TOKEN_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberService memberService;

    public String createNewAccessToken(String refreshToken) {
        // 토큰 유효성 검사에 실패하면 예외 발생
        if (!jwtTokenProvider.isExpired(refreshToken)) {
            throw new CustomException(TOKEN_NOT_FOUND);
        }

        Long memberId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        Member member = memberService.findById(memberId);

        return jwtTokenProvider.generateToken(member, Duration.ofHours(1));
    }
}