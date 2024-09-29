package com.ll.goohaeyou.member.member.application.dto;

import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.member.member.domain.type.Gender;

import java.time.LocalDate;

public record MemberResponse(
        Long id,
        String username,
        Gender gender,
        String location,
        LocalDate birth,
        String name,
        String phoneNumber,
        String email,
        boolean authenticated,
        String profileImageUrl
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getUsername(),
                member.getGender(),
                member.getLocation(),
                member.getBirth(),
                member.getName(),
                member.getPhoneNumber(),
                member.getEmail(),
                member.isAuthenticated(),
                member.getProfileImageUrl()
        );
    }
}
