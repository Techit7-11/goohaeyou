package com.ll.gooHaeYu.domain.member.member.controller;

import com.ll.gooHaeYu.domain.member.member.dto.AddMemberRequest;
import com.ll.gooHaeYu.domain.member.member.dto.LoginMemberRequest;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;
    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody AddMemberRequest request) {
        memberService.join(request);
        return  ResponseEntity.ok()
                .body("회원가입이 성공 했습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginMemberRequest request) {
        String token = memberService.login(request);
        return ResponseEntity.ok()
                .body(token);
    }
}
