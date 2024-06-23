package com.ll.goohaeyou.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class TossPaymentsConfig {

    @Value("${tossPayments.clientKey}")
    private String tossClientKey;

    @Value("${tossPayments.secretKey}")
    private String tossSecretKey;

    @Value("${tossPayments.successUrl}")
    private String successUrl;

    @Value("${tossPayments.failUrl}")
    private String failUrl;

    @Value("${tossPayments.url}")
    private String url;
}
