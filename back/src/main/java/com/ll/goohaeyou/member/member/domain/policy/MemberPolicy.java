package com.ll.goohaeyou.member.member.domain.policy;

import com.ll.goohaeyou.auth.exception.AuthException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MemberPolicy {
    BCryptPasswordEncoder bCryptPasswordEncoder;

    // 로그인 가능 여부 검증 (비밀번호 확인)
    public void verifyPassword(String MemberPassword, String inputPassword) {
        if (!bCryptPasswordEncoder.matches(MemberPassword, inputPassword)) {
            throw new AuthException.InvalidPasswordException();
        }
    }
}
