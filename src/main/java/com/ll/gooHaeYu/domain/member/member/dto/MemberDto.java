package com.ll.gooHaeYu.domain.member.member.dto;

import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.entity.type.Gender;
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
    private String location;
    private LocalDate birth;
    private String name;
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