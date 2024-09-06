package com.ll.goohaeyou.member.member.application.dto;

import com.ll.goohaeyou.member.member.domain.type.Gender;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ModifyMemberRequest(
        Gender gender,
        String location,
        LocalDate birth,

        @Size(min = 8, max = 20, message = "비밀번호는 최소 8자 ~ 최대 20자 이내로 입력해주세요.")
        String password
) {}
