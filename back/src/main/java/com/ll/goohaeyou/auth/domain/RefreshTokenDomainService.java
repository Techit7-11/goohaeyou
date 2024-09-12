package com.ll.goohaeyou.auth.domain;

import com.ll.goohaeyou.global.standard.anotations.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@DomainService
@RequiredArgsConstructor
public class RefreshTokenDomainService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void save(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }
}
