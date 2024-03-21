package com.ll.utils;

import com.ll.gooHaeYu.domain.member.member.dto.JoinForm;
import com.ll.gooHaeYu.domain.member.member.entity.type.Gender;

import java.time.LocalDate;

public class JoinFormBuilder {
    private String username = "user1";
    private String password = "password123";
    private String name = "김일번";
    private String phoneNumber = "01012345678";
    private Gender gender = Gender.MALE;
    private String location = "경기 성남시 분당구 판교역로 4";
    private LocalDate birth = LocalDate.of(2000, 1, 1);

    public JoinFormBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public JoinFormBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public JoinFormBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public JoinFormBuilder setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public JoinFormBuilder setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public JoinFormBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public JoinFormBuilder setBirth(LocalDate birth) {
        this.birth = birth;
        return this;
    }

    public JoinForm build() {
        return new JoinForm(username, password, name, phoneNumber, gender, location, birth);
    }
}
