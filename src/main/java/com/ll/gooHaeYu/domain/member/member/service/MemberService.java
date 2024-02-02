package com.ll.gooHaeYu.domain.member.member.service;

import com.ll.gooHaeYu.domain.member.member.dto.*;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.entity.type.Role;
import com.ll.gooHaeYu.domain.member.member.repository.MemberRepository;
import com.ll.gooHaeYu.global.config.AppConfig;
import com.ll.gooHaeYu.global.exception.CustomException;
import com.ll.gooHaeYu.global.exception.ErrorCode;
import com.ll.gooHaeYu.global.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletResponse response;

    @Transactional
    public Long join(JoinForm form) {
        memberRepository.findByUsername(form.getUsername())
                .ifPresent(member -> {
                    throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
                });

        Role role = form.getUsername().equals("admin") ? Role.ADMIN : Role.USER;

        Member newMember = memberRepository.save(Member.builder()
                .username(form.getUsername())
                .password(bCryptPasswordEncoder.encode(form.getPassword()))
                .name(form.getName())
                .phoneNumber(form.getPhoneNumber())
                .gender(form.getGender())
                .location(form.getLocation())
                .birth(form.getBirth())
                .role(role)
                .build());

        return newMember.getId();
    }

    public AuthAndMakeTokensResponse login(LoginForm form) {
        Member member = getMember(form.getUsername());

        if (!bCryptPasswordEncoder.matches(form.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createJwt(member.getUsername());
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setPath("/");
        cookie.setDomain(AppConfig.getSiteCookieDomain());
        //cookie.setMaxAge();
        response.addCookie(cookie);

        return AuthAndMakeTokensResponse.builder()
                .memberDto(MemberDto.fromEntity(member))
                .accessToken(accessToken)
                .refreshToken(null)
                .build();
    }

    public Member getMember(String username){
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(()->
                        new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return member;
    }

    public MemberDto findByUsername(String username) {
        Member member = getMember(username);

        return MemberDto.fromEntity(member);
    }

    @Transactional
    public void modifyMember(String username, MemberForm.Modify form) {
        Member member = getMember(username);

        String password = (form.getPassword() != null && !form.getPassword().isBlank())
                ? bCryptPasswordEncoder.encode(form.getPassword())
                : null;

        member.update(password, form.getGender(), form.getLocation(), form.getBirth());
    }

    public String findUsernameById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return member.getUsername();
    }
}
