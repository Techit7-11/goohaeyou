package com.ll.gooHaeYu.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey = "secretKey";
    static final long EXPIRED_MS = 1000 * 60 * 60;

    public String createJwt(String username) {
        return Jwts.builder()
                .claim("username", username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRED_MS))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean isExpired(String token) {
        return parseClaims(token)
                .getExpiration()
                .before(new Date());
    }

    public String getUsername(String token) {
        return parseClaims(token)
                .get("username", String.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
