package com.ll.gooHaeYu.domain.payment.payment.service;

import com.ll.gooHaeYu.domain.payment.payment.entity.Payment;
import com.ll.gooHaeYu.domain.payment.payment.repository.PaymentRepository;
import com.ll.gooHaeYu.global.config.TossPaymentsConfig;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.hibernate.mapping.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

import static com.ll.gooHaeYu.global.exception.ErrorCode.ALREADY_CANCELED;
import static com.ll.gooHaeYu.global.exception.ErrorCode.PAYMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PaymentCancelService {
    private final TossPaymentsConfig tossPaymentsConfig;
    private final PaymentRepository paymentRepository;

    @Transactional
    public Map tossPaymentCancel(String username, String paymentKey, String cancelReason) {
        Payment payment = paymentRepository.findByPaymentKeyAndMemberUsername(paymentKey, username)
                .orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));

        validatePaymentNotCanceled(payment);

        payment.cancelPayment(cancelReason);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        JSONObject params  = new JSONObject();
        params.put("cancelReason", cancelReason);

        return restTemplate.postForObject(TossPaymentsConfig.URL + paymentKey + "/cancel",
                new HttpEntity<>(params, headers),
                Map.class);
    }

    private void validatePaymentNotCanceled(Payment payment) {
        if (payment.isCanceled()) {
            throw new CustomException(ALREADY_CANCELED);
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedAuthKey = new String(
                Base64.getEncoder()
                        .encode((tossPaymentsConfig.getTossSecretKey() + ":")
                                .getBytes(StandardCharsets.UTF_8)));

        headers.setBasicAuth(encodedAuthKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return headers;
    }
}
