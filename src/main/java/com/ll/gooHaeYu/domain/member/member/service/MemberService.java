package com.ll.gooHaeYu.domain.member.member.service;

import com.ll.gooHaeYu.domain.member.member.dto.AddUserRequest;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.repository.MemberRepository;
import com.ll.gooHaeYu.global.exception.CustomException;
import com.ll.gooHaeYu.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public String join(AddUserRequest dto) {
        memberRepository.findByUsername(dto.getUsername())
                .ifPresent(member -> {
                    throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
                });

        Member member = Member.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .build();
        MemberRepository.save(member);

        return "SUCCESS";
    }
}