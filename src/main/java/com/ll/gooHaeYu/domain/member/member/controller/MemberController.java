package com.ll.gooHaeYu.domain.member.member.controller;

import com.ll.gooHaeYu.domain.member.member.dto.AddMemberForm;
import com.ll.gooHaeYu.domain.member.member.dto.LoginMemberRequest;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.net.URI;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody @Valid AddMemberForm request) {
        Long userId = memberService.join(request);
        return ResponseEntity.created(URI.create("/api/member/join" + userId)).build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginMemberRequest request) {
        String token = memberService.login(request);
        return ResponseEntity.ok()
                .body(token);
    }
}

dfkwejkrjlkwjklrewkljreklwjkldskldfskjklwe
dsfkljweklrjklew