package com.ll.goohaeyou.member.emailAuth.application;

import com.ll.goohaeyou.member.emailAuth.domain.service.AuthCodeDomainService;
import com.ll.goohaeyou.member.emailAuth.domain.service.EmailSenderDomainService;
import com.ll.goohaeyou.member.member.domain.service.MemberDomainService;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.member.emailAuth.domain.policy.EmailAuthPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailAuthService {
    private static final String AUTH_CODE_PREFIX = "authCode:";

    private final MemberDomainService memberDomainService;
    private final EmailAuthPolicy emailAuthPolicy;
    private final AuthCodeDomainService authCodeDomainService;
    private final EmailSenderDomainService emailSenderDomainService;

    @Transactional
    public void sendEmail(String username, String email) {
        Member member = memberDomainService.getByUsername(username);

        emailAuthPolicy.verifyAlreadyAuthenticated(member);

        String authCode = authCodeDomainService.generateAndStoreAuthCode(username);

        emailSenderDomainService.sendAuthCodeEmail(email, authCode);
    }

    @Transactional
    public void confirmVerification(String username, String inputCode) {
        String storedAuthCode = authCodeDomainService.getAuthCode(username);

        emailAuthPolicy.verifyAuthCode(inputCode, storedAuthCode);
        authCodeDomainService.deleteAuthCode(AUTH_CODE_PREFIX + username);

        Member member = memberDomainService.getByUsername(username);

        member.authenticate();
    }
}
