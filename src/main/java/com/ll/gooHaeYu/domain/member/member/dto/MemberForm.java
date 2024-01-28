package com.ll.gooHaeYu.domain.member.member.dto;

import com.ll.gooHaeYu.domain.member.member.entity.type.Gender;
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
    }
}
