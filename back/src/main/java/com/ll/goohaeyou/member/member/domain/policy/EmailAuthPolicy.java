package com.ll.goohaeyou.member.member.domain.policy;

import com.ll.goohaeyou.member.emailAuth.exception.EmailAuthException;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class EmailAuthPolicy {

    public void verifyAlreadyAuthenticated(Member member) {
        if (member.isAuthenticated()) {
            throw new EmailAuthException.EmailAlreadyAuthenticatedException();
        }
    }

    public void verifyAuthCode(String inputCode, String storedAuthCode) {
        if (storedAuthCode == null) {
            throw new EmailAuthException.InitiateEmailRequestException();
        }

        if (!inputCode.equals(storedAuthCode)) {
            throw new EmailAuthException.InvalidAuthCodeException();
        }
    }
}
