package com.ll.gooHaeYu.standard.base.util;

import com.ll.gooHaeYu.global.config.TossPaymentsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class TossPaymentUtil {
    private final TossPaymentsConfig tossPaymentsConfig;

    public HttpHeaders createBasicAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedAuthKey = Base64.getEncoder()
                .encodeToString((tossPaymentsConfig.getTossSecretKey() + ":").getBytes(StandardCharsets.UTF_8));

        headers.setBasicAuth(encodedAuthKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return headers;
    }
}
