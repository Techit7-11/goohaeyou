package com.ll.goohaeyou.domain.member.member.dto;

import com.ll.goohaeyou.domain.member.member.entity.type.Gender;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class MemberForm {
    @Getter
    @Setter
    public static class Modify {
        private Gender gender;
        private String location;
        private LocalDate birth;

        @Size(min = 8, max = 20, message = "비밀번호는 최소 8자 ~ 최대 20자 이내로 입력해주세요.")
        private String password;
    }
}
