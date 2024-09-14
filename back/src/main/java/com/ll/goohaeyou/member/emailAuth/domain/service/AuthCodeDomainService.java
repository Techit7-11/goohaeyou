package com.ll.goohaeyou.member.emailAuth.domain.service;

import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.member.emailAuth.domain.AuthCodeStorage;
import com.ll.goohaeyou.member.emailAuth.exception.EmailAuthException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@DomainService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthCodeDomainService {
    private static final long EXPIRATION_IN_SECONDS = 1800; // 30분

    private final AuthCodeStorage authCodeStorage;

    @Transactional
    public String generateAndStoreAuthCode(String username) {
        String randomKey = RandomStringUtils.randomAlphanumeric(5);

        if (authCodeStorage.getData(username) != null) {
            authCodeStorage.deleteData(username);
        }

        authCodeStorage.setDataExpire(username, randomKey, EXPIRATION_IN_SECONDS);
        return randomKey;
    }

    @Transactional
    public void deleteAuthCode(String username) {
        authCodeStorage.deleteData(username);
    }

    public String getAuthCode(String username) {
        String storedAuthCode = authCodeStorage.getData(username);

        if (Objects.isNull(storedAuthCode)) {
            throw new EmailAuthException.InitiateEmailRequestException();
        }

        return storedAuthCode;
    }
}
