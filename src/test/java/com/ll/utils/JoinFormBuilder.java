package com.ll.utils;

import com.ll.gooHaeYu.domain.member.member.dto.JoinForm;
import com.ll.gooHaeYu.domain.member.member.entity.type.Gender;
import lombok.Setter;
import java.time.LocalDate;

public class JoinFormBuilder {
    @Setter private String username = "user1";
    @Setter private String password = "password123";
    @Setter private String name = "김일번";
    @Setter private String phoneNumber = "01012345678";
    @Setter private Gender gender = Gender.MALE;
    @Setter private String location = "경기 성남시 분당구 판교역로 4";
    @Setter private LocalDate birth = LocalDate.of(2000, 1, 1);

    public JoinForm build() {
        return new JoinForm(username, password, name, phoneNumber, gender, location, birth);
    }
}
