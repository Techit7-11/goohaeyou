package com.ll.utils.builder;

import com.ll.goohaeyou.member.member.application.dto.JoinRequest;
import com.ll.goohaeyou.member.member.domain.type.Gender;

import java.time.LocalDate;

public class JoinRequestBuilder {
    private String username = "user1";
    private String password = "password123";
    private String name = "김일번";
    private String phoneNumber = "01012345678";
    private String email = "test@example.com";
    private Gender gender = Gender.MALE;
    private String location = "경기 성남시 분당구 판교역로 4";
    private LocalDate birth = LocalDate.of(2000, 1, 1);

    public JoinRequestBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public JoinRequestBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public JoinRequestBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public JoinRequestBuilder setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public JoinRequestBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public JoinRequestBuilder setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public JoinRequestBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public JoinRequestBuilder setBirth(LocalDate birth) {
        this.birth = birth;
        return this;
    }

    public JoinRequest build() {
        return new JoinRequest(username, password, name, phoneNumber, email, gender, location, birth);
    }
}
