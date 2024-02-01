package com.ll.gooHaeYu.domain.member.member.dto;

import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.entity.type.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDate;

@Builder
@Getter
public class MemberDto {
    @NonNull
    private Long id;
    @NonNull
    private String username;
    @NonNull
    private Gender gender;
    @NonNull
    private String location;
    @NonNull
    private LocalDate birth;
    @NonNull
    private String name;
    @NonNull
    private String phoneNumber;

    public static MemberDto fromEntity(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .gender(member.getGender())
                .location(member.getLocation())
                .birth(member.getBirth())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }
}