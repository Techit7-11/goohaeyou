package com.ll.gooHaeYu.domain.member.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

public class User {
    private String name;
    private String birthDate;
    private String gender;

    @RestController
    public class UserController {

        @GetMapping("/mypage")
        public User myPage() {
            User user = new User();
            user.Name("홍길동");
            user.BirthDate("2000-01-01");
            user.Gender("남성");

            return user;
        }

    }