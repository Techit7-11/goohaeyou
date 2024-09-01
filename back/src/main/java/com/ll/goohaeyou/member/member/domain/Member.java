package com.ll.goohaeyou.member.member.domain;

import com.ll.goohaeyou.member.member.application.dto.SocialProfileForm;
import com.ll.goohaeyou.member.member.domain.type.Gender;
import com.ll.goohaeyou.member.member.domain.type.Level;
import com.ll.goohaeyou.member.member.domain.type.Role;
import com.ll.goohaeyou.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "member")
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    private String name;

    private String phoneNumber;

    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.GUEST;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Gender gender = Gender.UNDEFINED;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Level level = Level.LV1;

    private int transactionCount;

    private String location;

    private LocalDate birth;

    private boolean authenticated;

    private long restCash;

    private String FCMToken;

    private String profileImageUrl;

    private int regionCode;

    public void update(String password, Gender gender, String location, LocalDate birth) {
        if (location != null && !location.isBlank()) {
            this.location = location;
        }
        if (birth != null) {
            this.birth = birth;
        }
        if (gender != null) {
            this.gender = gender;
        }
        if (password != null && !password.isBlank()) {
            this.password = password;
        }
    }

    public void updateRole(Role role) {
        this.role = role;
    }

    public Member oauthUpdate(String username) {
        this.username = username;
        return this;
    }

    public void oauthDetailUpdate(SocialProfileForm form) {
        this.name = form.getName();
        this.phoneNumber = form.getPhoneNumber();
        this.gender = form.getGender();
        this.location = form.getLocation();
        this.birth = form.getBirth();
    }

    public void updateRestCash(long cash) {
        this.restCash = cash;
    }

    public void increaseTransactionCount() {
        this.transactionCount += 1;
        updateLevel(this.transactionCount);
    }

    public void updateLevel(int transactionCount) {
        this.level = Level.getLevelByTransactionCount(transactionCount);
    }

    public void addRestCash(int earn) {
        this.restCash += earn;
    }

    public void authenticate() {
        this.authenticated = true;
    }

    public void registerFCMToken(String token) {
        this.FCMToken = token;
    }

    public void removeFCMToken() {
        this.FCMToken = null;
    }

    public void setImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
