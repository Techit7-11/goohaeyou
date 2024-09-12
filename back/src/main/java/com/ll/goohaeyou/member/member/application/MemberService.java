package com.ll.goohaeyou.member.member.application;

import com.ll.goohaeyou.member.member.application.dto.*;
import com.ll.goohaeyou.member.member.domain.MemberDomainService;
import com.ll.goohaeyou.member.member.domain.MemberReader;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberReader memberReader;
    private final MemberDomainService memberDomainService;

    @Transactional
    public void join(JoinRequest request) {
        memberDomainService.joinMember(request);
    }

    public void login(LoginRequest request) {
        Member member = memberReader.getMemberByUsername(request.username());

        memberDomainService.verifyPassword(member.getPassword(), request.password());
    }

    public MemberResponse findByUsername(String username) {
        Member member = memberReader.getMemberByUsername(username);

        return MemberResponse.from(member);
    }

    @Transactional
    public void modifyMember(String username, ModifyMemberRequest request) {
        Member member = memberReader.getMemberByUsername(username);

        member.update(request.password(), request.gender(), request.location(), request.birth());
    }

    @Transactional
    public MemberResponse updateSocialMemberProfile(String username, UpdateSocialProfileRequest request) {
        Member member = memberReader.getMemberByUsername(username);

        member.oauthDetailUpdate(request);

        return MemberResponse.from(member);
    }
}
