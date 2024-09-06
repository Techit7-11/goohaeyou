package com.ll.goohaeyou.member.member.application;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.global.standard.base.util.Util;
import com.ll.goohaeyou.member.member.application.dto.*;
import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
import com.ll.goohaeyou.member.member.domain.type.Role;
import com.ll.goohaeyou.member.member.exception.MemberException;
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

    @Transactional
    public void join(JoinRequest request) {
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

    public void login(LoginRequest request) {
        Member member = getMember(request.username());

        if (!bCryptPasswordEncoder.matches(request.password(), member.getPassword())) {
            throw new AuthException.InvalidPasswordException();
        }
    }

    public Member getMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(MemberException.MemberNotFoundException::new);
    }

    public MemberResponse findByUsername(String username) {
        Member member = getMember(username);

        return MemberResponse.from(member);
    }

    @Transactional
    public void modifyMember(String username, ModifyMemberRequest request) {
        Member member = getMember(username);

        String password = (request.password() != null && !request.password().isBlank())
                ? bCryptPasswordEncoder.encode(request.password())
                : null;

        member.update(password, request.gender(), request.location(), request.birth());
    }

    public Member findById(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(MemberException.MemberNotFoundException::new);
    }

    @Transactional
    public MemberResponse updateSocialMemberProfile(String username, SocialProfileForm form) {
        Member member = getMember(username);

        if (member.getEmail() == null) {
            member.authenticate();
        }

        member.oauthDetailUpdate(form);
        member.updateRole(Role.USER);

        return MemberResponse.from(member);
    }
}
