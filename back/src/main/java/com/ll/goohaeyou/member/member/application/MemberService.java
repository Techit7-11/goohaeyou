package com.ll.goohaeyou.member.member.application;

import com.ll.goohaeyou.member.member.application.dto.*;
import com.ll.goohaeyou.member.member.domain.service.MemberDomainService;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberDomainService memberDomainService;

    @Transactional
    public void join(JoinRequest request) {
        memberDomainService.joinMember(request);
    }

    public void login(LoginRequest request) {
        Member member = memberDomainService.getByUsername(request.username());

        memberDomainService.verifyPassword(request.password(), member.getPassword());
    }

    public MemberResponse findByUsername(String username) {
        Member member = memberDomainService.getByUsername(username);

        return MemberResponse.from(member);
    }

    @Transactional
    public void modifyMember(String username, ModifyMemberRequest request) {
        Member member = memberDomainService.getByUsername(username);

        member.update(request.password(), request.gender(), request.location(), request.birth());
    }

    @Transactional
    public MemberResponse updateSocialMemberProfile(String username, UpdateSocialProfileRequest request) {
        Member member = memberDomainService.getByUsername(username);

        member.oauthDetailUpdate(request);

        return MemberResponse.from(member);
    }
}
