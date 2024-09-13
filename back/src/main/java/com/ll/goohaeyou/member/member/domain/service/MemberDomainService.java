package com.ll.goohaeyou.member.member.domain.service;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.global.standard.base.util.Util;
import com.ll.goohaeyou.member.member.application.dto.JoinRequest;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
import com.ll.goohaeyou.member.member.domain.type.Role;
import com.ll.goohaeyou.member.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;

@DomainService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberDomainService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void joinMember(JoinRequest request) {
        memberRepository.findByUsername(request.username())
                .ifPresent(member -> {
                    throw new MemberException.DuplicateUsernameException();
                });

        Role role = "admin".equals(request.username()) ? Role.ADMIN : Role.USER;

        if (role == Role.ADMIN) {
            memberRepository.save(
                    Member.createAdmin(
                            bCryptPasswordEncoder.encode(request.password()),
                            request.name(),
                            request.phoneNumber(),
                            request.email(),
                            request.gender(),
                            request.location(),
                            request.birth(),
                            Util.Region.getRegionCodeFromAddress(request.location())
                    )
            );
        }
        if (role == Role.USER) {
            memberRepository.save(
                    Member.createUser(
                            request.username(),
                            bCryptPasswordEncoder.encode(request.password()),
                            request.name(),
                            request.phoneNumber(),
                            request.email(),
                            request.gender(),
                            request.location(),
                            request.birth(),
                            Util.Region.getRegionCodeFromAddress(request.location())
                    )
            );
        }
    }

    @Transactional
    public void saveOrUpdateMemberByEmail(String email) {
        Member member = memberRepository.findByUsername(email)
                .map(entity -> entity.oauthUpdate(email))
                .orElseGet(() -> Member.createSocialGoogle(email));

        memberRepository.save(member);
    }

    @Transactional
    public void save(Member member) {
        memberRepository.save(member);
    }

    public void verifyPassword(String inputPassword, String memberPassword) {
        if (!bCryptPasswordEncoder.matches(inputPassword, memberPassword)) {
            throw new AuthException.InvalidPasswordException();
        }
    }

    public Member getByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(MemberException.MemberNotFoundException::new);
    }

    public Member getById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(MemberException.MemberNotFoundException::new);
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName();
    }
}