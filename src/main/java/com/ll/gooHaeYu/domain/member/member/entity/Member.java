package com.ll.gooHaeYu.domain.member.member.entity;

import com.ll.gooHaeYu.domain.member.member.entity.type.Gender;
import com.ll.gooHaeYu.domain.member.member.entity.type.Role;
import com.ll.gooHaeYu.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
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

    @Column(unique = true)
    private String username;

    private String password;

    private String name;

    private String phoneNumber;   // 01012341234

    @Enumerated(EnumType.STRING)
    private Role role = Role.GUEST;

    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.UNDEFINED;

    private String location;

    private LocalDate birth;  // yyyy-MM-dd

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

    public Member OauthUpdate(String username) {
        this.username = username;
        return this;
    }

}
