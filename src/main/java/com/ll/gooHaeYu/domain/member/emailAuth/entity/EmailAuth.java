package com.ll.gooHaeYu.domain.member.emailAuth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Builder
@Getter
@Table(name = "email_auth")
public class EmailAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Email
    private String email;

    private String authCode;

    private LocalDateTime expiredAt;

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public void setExpiredAt(LocalDateTime localDateTime) {
        this.expiredAt = localDateTime;
    }
}
