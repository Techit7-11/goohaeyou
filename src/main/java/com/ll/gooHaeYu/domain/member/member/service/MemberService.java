package com.ll.gooHaeYu.domain.member.member.service;

import com.ll.gooHaeYu.domain.member.member.dto.AddMemberRequest;
import com.ll.gooHaeYu.domain.member.member.dto.LoginMemberRequest;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.repository.MemberRepository;
import com.ll.gooHaeYu.global.exception.CustomException;
import com.ll.gooHaeYu.global.exception.ErrorCode;
import com.ll.gooHaeYu.standard.base.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${jwt.secret}")
    private String secretKey;
    private Long expiredMs = 1000 * 60 * 60l;

    public String join(AddMemberRequest dto) {
        memberRepository.findByUsername(dto.getUsername())
                .ifPresent(member -> {
                    throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
                });

        memberRepository.save(Member.builder()
                .username(dto.getUsername())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .build());

        return "SUCCESS";
    }

    public String login(LoginMemberRequest dto) {
        Member member = memberRepository.findByUsername(dto.getUsername())
                .orElseThrow(() ->
                        new CustomException(ErrorCode.LOGIN_FAIL));

        if (!bCryptPasswordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        return JwtUtil.createJwt(member.getUsername(), secretKey, expiredMs);
    }
}
