package com.ll.goohaeyou.member.emailAuth.domain.service;

import com.ll.goohaeyou.global.standard.anotations.DomainService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@DomainService
@RequiredArgsConstructor
public class EmailSenderDomainService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    private static final String EMAIL_SUBJECT = "[From 구해유] 이메일 인증을 위한 인증코드가 발급되었습니다.";
    private static final String EMAIL_TEMPLATE = "emailTemplate";

    @Transactional
    public void sendAuthCodeEmail(String email, String authCode) {
        Context context = new Context();
        context.setVariable("authCode", authCode);
        String htmlContent = templateEngine.process(EMAIL_TEMPLATE, context);

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
}
