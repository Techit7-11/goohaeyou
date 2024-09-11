package com.ll.goohaeyou.member.emailAuth.application;

import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.member.emailAuth.domain.AuthCodeStorage;
import com.ll.goohaeyou.member.emailAuth.exception.EmailAuthException;
import com.ll.goohaeyou.member.member.domain.policy.EmailAuthPolicy;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailAuthService {
    private static final String EMAIL_SUBJECT = "[From 구해유] 이메일 인증을 위한 인증코드가 발급되었습니다.";
    private static final String EMAIL_TEMPLATE = "emailTemplate";
    private static final long EXPIRATION_IN_SECONDS = 1800;   // 30 minutes

    private final MemberRepository memberRepository;
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    private final AuthCodeStorage authCodeStorage;
    private final EmailAuthPolicy emailAuthPolicy;

    @Transactional
    public void sendEmail(String username, String email) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);

        emailAuthPolicy.verifyAlreadyAuthenticated(member);

        String authCode = manageMail(username);

        Context context = new Context();
        context.setVariable("authCode", authCode);
        String htmlContent = templateEngine.process(EMAIL_TEMPLATE, context);

        sendEmailToUser(email, htmlContent);
    }

    @Transactional
    public String manageMail(String username) {
        String randomKey = RandomStringUtils.randomAlphanumeric(5);

        if (authCodeStorage.getData(username) != null) {
            authCodeStorage.deleteData(username);
        }

        authCodeStorage.setDataExpire(username, randomKey, EXPIRATION_IN_SECONDS);


        return randomKey;
    }

    private void sendEmailToUser(String email, String htmlContent) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(email);
            helper.setSubject(EMAIL_SUBJECT);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

    @Transactional
    public void confirmVerification(String username, String inputCode) {
        String authCode = authCodeStorage.getData(username);

        if (authCode == null) {
            throw new EmailAuthException.InitiateEmailRequestException();
        }

        emailAuthPolicy.verifyAuthCode(inputCode, authCode);

        authCodeStorage.deleteData(username);

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);
        member.authenticate();
    }
}
