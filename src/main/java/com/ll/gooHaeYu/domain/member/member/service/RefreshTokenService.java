package com.ll.gooHaeYu.domain.member.member.service;

import com.ll.gooHaeYu.domain.member.member.entity.RefreshToken;
import com.ll.gooHaeYu.domain.member.member.repository.RefreshTokenRepository;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.ll.gooHaeYu.global.exception.ErrorCode.TOKEN_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(TOKEN_NOT_FOUND));
    }
}
