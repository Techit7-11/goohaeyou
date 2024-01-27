package com.ll.gooHaeYu.domain.member.member.service;

import com.ll.gooHaeYu.domain.member.member.dto.JoinForm;
import com.ll.gooHaeYu.domain.member.member.dto.LoginForm;
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

    @Transactional
    public Long join(JoinForm form) {
        memberRepository.findByUsername(form.getUsername())
                .ifPresent(member -> {
                    throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
                });

        Member newMember = memberRepository.save(Member.builder()
                .username(form.getUsername())
                .password(bCryptPasswordEncoder.encode(form.getPassword()))
                .build());

        return newMember.getId();
    }

    public String login(LoginForm form) {
        Member member = getMember(form.getUsername());

        if (!bCryptPasswordEncoder.matches(form.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        return JwtUtil.createJwt(member.getUsername(), secretKey, expiredMs);
    }

    public Member getMember(String username){
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(()->
                        new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return member;
    }
}
