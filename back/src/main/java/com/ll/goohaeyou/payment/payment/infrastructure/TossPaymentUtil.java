package com.ll.goohaeyou.payment.payment.infrastructure;

import com.ll.goohaeyou.global.config.TossPaymentsConfig;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class TossPaymentUtil {
    private final TossPaymentsConfig tossPaymentsConfig;
    private final RestTemplate restTemplate;

    public HttpHeaders createBasicAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedAuthKey = Base64.getEncoder()
                .encodeToString((tossPaymentsConfig.getTossSecretKey() + ":").getBytes(StandardCharsets.UTF_8));

        headers.setBasicAuth(encodedAuthKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return headers;
    }

    // 공통 HTTP 요청 메서드
    private <T> T sendRequest(String endpoint, JSONObject params, Class<T> responseType) {
        HttpHeaders headers = createBasicAuthHeaders();
        HttpEntity<JSONObject> entity = new HttpEntity<>(params, headers);

        return restTemplate.postForObject(tossPaymentsConfig.getUrl() + endpoint,
                entity, responseType);
    }

    // 결제 성공 요청 메서드
    public <T> T sendPaymentRequest(String paymentKey, JSONObject params, Class<T> responseType) {
        return sendRequest(paymentKey, params, responseType);
    }

    // 결제 취소 요청 메서드
    public <T> T sendPaymentCancelRequest(String paymentKey, JSONObject params, Class<T> responseType) {
        return sendRequest(paymentKey + "/cancel", params, responseType);
    }
}
