package com.ll.gooHaeYu.domain.payment.payment.service;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.application.application.entity.type.DepositStatus;
import com.ll.gooHaeYu.domain.application.application.service.ApplicationService;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import com.ll.gooHaeYu.domain.payment.payment.dto.PaymentReqDto;
import com.ll.gooHaeYu.domain.payment.payment.dto.PaymentResDto;
import com.ll.gooHaeYu.domain.payment.payment.dto.fail.PaymentFailDto;
import com.ll.gooHaeYu.domain.payment.payment.dto.success.PaymentSuccessDto;
import com.ll.gooHaeYu.domain.payment.payment.entity.Payment;
import com.ll.gooHaeYu.domain.payment.payment.entity.type.PayType;
import com.ll.gooHaeYu.domain.payment.payment.repository.PaymentRepository;
import com.ll.gooHaeYu.global.config.TossPaymentsConfig;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

import static com.ll.gooHaeYu.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final TossPaymentsConfig tossPaymentsConfig;
    private final RestTemplate restTemplate;
    private final MemberService memberService;
    private final ApplicationService applicationService;

    @Transactional
    public PaymentResDto requestTossPayment(PaymentReqDto paymentReqDto, String username) {
        Payment payment = paymentReqDto.toEntity();
        payment.updatePayer(memberService.getMember(username));

        PaymentResDto paymentResDto = paymentRepository.save(payment).toPaymentRespDto();

        paymentResDto.setSuccessUrl(tossPaymentsConfig.getSuccessUrl());
        paymentResDto.setFailUrl(tossPaymentsConfig.getFailUrl());

        return paymentResDto;
    }

    @Transactional
    public PaymentSuccessDto tossPaymentSuccess(String paymentKey, String orderId, Long amount) {
        Payment payment = verifyPayment(orderId, amount);

        PaymentSuccessDto successDto = requestPaymentAccept(paymentKey, orderId, amount);

        payment.updatePaymentKey(paymentKey);
        payment.markAsPaid();
        payment.recordApprovedAt(successDto.getApprovedAt());

        Application application = applicationService.findByIdAndValidate(payment.getApplicationId());
        application.updateDepositStatus(DepositStatus.DEPOSIT_PAID);

        updatePaymentType(payment, successDto.getMethod());

        return successDto;
    }

    private void updatePaymentType(Payment payment, String method) {
        PayType payType = PayType.findByMethod(method);
        payment.updatePayType(payType);
    }

    public Payment verifyPayment(String orderId, Long amount) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (!payment.getTotalAmount().equals(amount)) {
            throw new CustomException(PAYMENT_AMOUNT_MISMATCH);
        }

        return payment;
    }

    @Transactional
    public PaymentSuccessDto requestPaymentAccept(String paymentKey, String orderId, Long amount) {
        HttpHeaders headers = createBasicAuthHeaders();
        JSONObject params = createPaymentRequestParams(orderId, amount);

        try {
            return restTemplate.postForObject(TossPaymentsConfig.URL + paymentKey,
                    new HttpEntity<>(params, headers),
                    PaymentSuccessDto.class);
        } catch (Exception e) {
            throw new CustomException(ALREADY_APPROVED);
        }
    }

    private HttpHeaders createBasicAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedAuthKey = Base64.getEncoder().encodeToString((tossPaymentsConfig.getTossSecretKey() + ":").getBytes(StandardCharsets.UTF_8));

        headers.setBasicAuth(encodedAuthKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return headers;
    }

    private JSONObject createPaymentRequestParams(String orderId, Long amount) {
        JSONObject params = new JSONObject();
        params.put("orderId", orderId);
        params.put("amount", amount);

        return params;
    }

    @Transactional
    public PaymentFailDto tossPaymentFail(String code, String message, String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));

        payment.markAsUnpaid();
        payment.recordFailReason(message);

        return PaymentFailDto.builder()
                .errorCode(code)
                .errorMessage(message)
                .orderId(orderId)
                .build();
    }
}
