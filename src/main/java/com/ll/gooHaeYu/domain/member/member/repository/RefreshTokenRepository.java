package com.ll.gooHaeYu.domain.member.member.repository;

import com.ll.gooHaeYu.domain.member.member.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByMemberId(Long memeberId);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}