package com.ll.gooHaeYu.domain.member.emailAuth.service;

import com.ll.gooHaeYu.domain.member.emailAuth.entity.EmailAuth;
import com.ll.gooHaeYu.domain.member.emailAuth.repository.EmailAuthRepository;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import com.ll.gooHaeYu.global.exception.CustomException;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static com.ll.gooHaeYu.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailAuthService {
    private final MemberService memberService;
    private final EmailAuthRepository emailAuthRepository;
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    @Transactional
    public void sendEmail(String username, String email) {
        verifyAlreadyAuthenticated(username);

        EmailAuth emailAuth = manageMail(username, email);

        Context context = new Context();
        context.setVariable("authCode", emailAuth.getAuthCode());
        String htmlContent = templateEngine.process("emailTemplate", context);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(email);
            helper.setSubject("[From 구해유] 이메일 인증을 위한 인증코드가 발급되었습니다.");
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

    public void verifyAlreadyAuthenticated(String username) {
        Member member = memberService.getMember(username);

        if (member.isAuthenticated()) {
            throw new CustomException(EMAIL_ALREADY_AUTHENTICATED);
        }
    }

    @Transactional
    public EmailAuth manageMail(String username, String email) {
        String randomKey = RandomStringUtils.randomAlphanumeric(5);

        Optional<EmailAuth> optionalEmailAuth = emailAuthRepository.findByUsername(username);

        if (optionalEmailAuth.isPresent()) {
            EmailAuth existEmail = optionalEmailAuth.get();
            existEmail.setAuthCode(randomKey);
            existEmail.setExpiredAt(LocalDateTime.now().plusMinutes(30));

            return existEmail;
        } else {
            EmailAuth newEmailAuth = EmailAuth.builder()
                    .username(username)
                    .email(email)
                    .authCode(randomKey)
                    .expiredAt(LocalDateTime.now().plusMinutes(30))
                    .build();

            emailAuthRepository.save(newEmailAuth);

            return newEmailAuth;
        }
    }

    @Transactional
    public void confirmVerification(String username, String authCode) {
        EmailAuth emailAuth = emailAuthRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(INITIATE_EMAIL_REQUEST));

        verifyCode(emailAuth, authCode);

        emailAuthRepository.delete(emailAuth);

        Member member = memberService.getMember(username);
        member.authenticate();
    }

    @Transactional
    public void verifyCode(EmailAuth emailAuth, String authCode) {
        if (emailAuth.getExpiredAt().isBefore(LocalDateTime.now())) {
            emailAuthRepository.delete(emailAuth);
            throw new CustomException(EXPIRED_AUTH_CODE);
        }

        if (!authCode.equals(emailAuth.getAuthCode())) {
            throw new CustomException(INVALID_AUTH_CODE);
        }
    }
}
