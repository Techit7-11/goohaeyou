package com.ll.gooHaeYu.domain.member.member.dto;

import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.entity.type.Gender;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class MemberDto {
    private Long id;
    private String username;
    private String password;
    private Gender gender;
    private String location;
    private LocalDate birth;

    public static MemberDto fromEntity(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .password(member.getPassword())
                .gender(member.getGender())
                .location(member.getLocation())
                .birth(member.getBirth())
                .build();
    }
}
