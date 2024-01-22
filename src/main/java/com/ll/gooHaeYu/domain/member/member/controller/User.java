package com.ll.gooHaeYu.domain.member.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

public class User {
    private String name;
    private String birthDate;
    private String gender;

    @RestController
    public class MyPageController {

        @GetMapping("/mypage")
        public User myPage() {
            // 현재 로그인된 사용자 정보를 가져오는 로직
            User user = getUserInfo(); // 사용자 정보를 가져오는 메서드를 구현해야 합니다.

            return user;
        }
    }
}