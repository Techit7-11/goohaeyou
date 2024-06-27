package com.ll.goohaeyou.domain.member.member.dto;

import com.ll.goohaeyou.domain.member.member.entity.Member;
import com.ll.goohaeyou.domain.member.member.entity.type.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class MemberDto {
    @NotNull
    private Long id;
    @NotBlank
    private String username;
    private Gender gender;
    @NotBlank
    private String location;
    private LocalDate birth;
    @NotBlank
    private String name;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String email;
    private boolean authenticated;
    private String profileImageUrl;

    public static MemberDto from(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .gender(member.getGender())
                .location(member.getLocation())
                .birth(member.getBirth())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .email(member.getEmail())
                .authenticated(member.isAuthenticated())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}