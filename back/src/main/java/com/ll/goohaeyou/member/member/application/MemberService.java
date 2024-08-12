package com.ll.goohaeyou.member.member.application;

import com.ll.goohaeyou.member.member.dto.*;
import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
import com.ll.goohaeyou.member.member.domain.type.Role;
import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.member.member.exception.MemberException;
import com.ll.goohaeyou.global.standard.base.util.Ut;
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
    public JoinForm join(JoinForm form) {
        memberRepository.findByUsername(form.getUsername())
                .ifPresent(member -> {
                    throw new MemberException.DuplicateUsernameException();
                });

        Role role = form.getUsername().equals("admin") ? Role.ADMIN : Role.USER;

        memberRepository.save(Member.builder()
                .username(form.getUsername())
                .password(bCryptPasswordEncoder.encode(form.getPassword()))
                .name(form.getName())
                .phoneNumber(form.getPhoneNumber())
                .email(form.getEmail())
                .gender(form.getGender())
                .location(form.getLocation())
                .birth(form.getBirth())
                .role(role)
                .regionCode(Ut.Region.getRegionCodeFromAddress(form.getLocation()))
                .build());

        return form;
    }

    public void login(LoginForm form) {
        Member member = getMember(form.getUsername());

        if (!bCryptPasswordEncoder.matches(form.getPassword(), member.getPassword())) {
            throw new AuthException.InvalidPasswordException();
        }
    }

    public Member getMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(MemberException.MemberNotFoundException::new);
    }

    public MemberDto findByUsername(String username) {
        Member member = getMember(username);

        return MemberDto.from(member);
    }

    @Transactional
    public void modifyMember(String username, MemberForm.Modify form) {
        Member member = getMember(username);

        String password = (form.getPassword() != null && !form.getPassword().isBlank())
                ? bCryptPasswordEncoder.encode(form.getPassword())
                : null;

        member.update(password, form.getGender(), form.getLocation(), form.getBirth());
    }

    public Member findById(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(MemberException.MemberNotFoundException::new);
    }

    @Transactional
    public MemberDto updateSocialMemberProfile(String username, SocialProfileForm form) {
        Member member = getMember(username);

        if (member.getEmail() == null) {
            member.authenticate();
        }

        member.oauthDetailUpdate(form);
        member.updateRole(Role.USER);

        return MemberDto.from(member);
    }
}
