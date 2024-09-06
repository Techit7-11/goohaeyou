package com.ll.goohaeyou.member.member.domain;

import com.ll.goohaeyou.member.member.application.dto.UpdateSocialProfileRequest;
import com.ll.goohaeyou.member.member.domain.type.Gender;
import com.ll.goohaeyou.member.member.domain.type.Level;
import com.ll.goohaeyou.member.member.domain.type.Role;
import com.ll.goohaeyou.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private Role role;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Level level;

    private int transactionCount;
    private String location;
    private LocalDate birth;
    private boolean authenticated;
    private long restCash;
    private String FCMToken;
    private String profileImageUrl;
    private int regionCode;

    private Member(
            String username,
            String password,
            String name,
            String phoneNumber,
            String email,
            Gender gender,
            String location,
            LocalDate birth,
            boolean authenticated,
            Role role,
            int regionCode
    ) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.gender = (gender == null) ? Gender.UNDEFINED : gender;
        this.location = location;
        this.birth = birth;
        this.authenticated = authenticated;
        this.role = (role == null) ? Role.GUEST : role;
        this.level = Level.LV1;    // Default level
        this.regionCode = regionCode;
    }

    public static Member createAdmin(
            String password,
            String name,
            String phoneNumber,
            String email,
            Gender gender,
            String location,
            LocalDate birth,
            int regionCode
    ) {
        return new Member(
                "admin",
                password,
                name,
                phoneNumber,
                email,
                gender,
                location,
                birth,
                false,
                Role.ADMIN,
                regionCode
        );
    }

    public static Member createSocialGoogle(
            String username
    ) {
        return new Member(
                username,
                null,
                null,
                null,
                null,
                Gender.UNDEFINED,
                null,
                null,
                true,
                Role.USER,
                0
        );
    }

    public static Member createUser(
            String username,
            String password,
            String name,
            String phoneNumber,
            String email,
            Gender gender,
            String location,
            LocalDate birth,
            int regionCode
    ) {
        return new Member(
                username,
                password,
                name,
                phoneNumber,
                email,
                gender,
                location,
                birth,
                false,
                Role.USER,
                regionCode
        );
    }

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

    public void oauthDetailUpdate(UpdateSocialProfileRequest request) {
        this.name = request.name();
        this.phoneNumber = request.phoneNumber();
        this.gender = request.gender();
        this.location = request.location();
        this.birth = request.birth();
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

    public void updateImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
