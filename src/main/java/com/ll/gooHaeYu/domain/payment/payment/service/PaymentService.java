package com.ll.gooHaeYu.domain.payment.payment.service;

import com.ll.gooHaeYu.domain.application.application.entity.type.DepositStatus;
import com.ll.gooHaeYu.domain.application.application.service.ApplicationService;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import com.ll.gooHaeYu.domain.payment.payment.dto.fail.PaymentFailDto;
import com.ll.gooHaeYu.domain.payment.payment.dto.request.PaymentReqDto;
import com.ll.gooHaeYu.domain.payment.payment.dto.request.PaymentResDto;
import com.ll.gooHaeYu.domain.payment.payment.dto.success.PaymentSuccessDto;
import com.ll.gooHaeYu.domain.payment.payment.entity.Payment;
import com.ll.gooHaeYu.domain.payment.payment.entity.type.PayType;
import com.ll.gooHaeYu.domain.payment.payment.repository.PaymentRepository;
import com.ll.gooHaeYu.global.config.TossPaymentsConfig;
import com.ll.gooHaeYu.global.exception.CustomException;
import com.ll.gooHaeYu.standard.base.util.TossPaymentUtil;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
    private final TossPaymentUtil tossPaymentUtil;

    @Transactional
    public PaymentResDto requestTossPayment(PaymentReqDto paymentReqDto, String username) {
        Payment payment = paymentReqDto.toEntity();
        payment.updatePayer(memberService.getMember(username));

        PaymentResDto paymentResDto = paymentRepository.save(payment).toPaymentRespDto();

        setRedirectUrls(paymentResDto);

        return paymentResDto;
    }

    private void setRedirectUrls(PaymentResDto paymentResDto) {
        paymentResDto.setSuccessUrl(tossPaymentsConfig.getSuccessUrl());
        paymentResDto.setFailUrl(tossPaymentsConfig.getFailUrl());
    }

    @Transactional
    public PaymentSuccessDto tossPaymentSuccess(String paymentKey, String orderId, Long amount) {
        Payment payment = verifyPayment(orderId, amount);
        PaymentSuccessDto successDto = requestPaymentAccept(paymentKey, orderId, amount);

        updatePaymentStatus(payment, successDto);
        applicationService.updateApplicationStatus(payment.getApplicationId(), DepositStatus.DEPOSIT_PAID);
        // TODO 충전_토스페이먼츠, 사용_토스페이먼츠 로그 남기기

        return successDto;
    }

    public Payment verifyPayment(String orderId, Long amount) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (!payment.getTotalAmount().equals(amount)) {
            throw new CustomException(PAYMENT_AMOUNT_MISMATCH);
        }

        return payment;
    }

    private void updatePaymentStatus(Payment payment, PaymentSuccessDto successDto) {
        payment.updatePaymentKey(successDto.getPaymentKey());
        payment.markAsPaid();
        payment.recordApprovedAt(successDto.getApprovedAt());
        updatePayTypeByPayment(payment, successDto.getMethod());
    }

    private void updatePayTypeByPayment(Payment payment, String method) {
        PayType payType = PayType.findByMethod(method);
        payment.updatePayType(payType);
    }

    @Transactional
    public PaymentSuccessDto requestPaymentAccept(String paymentKey, String orderId, Long amount) {
        HttpHeaders headers = tossPaymentUtil.createBasicAuthHeaders();
        JSONObject params = createPaymentRequestParams(orderId, amount);

        try {
            return restTemplate.postForObject(TossPaymentsConfig.URL + paymentKey,
                    new HttpEntity<>(params, headers),
                    PaymentSuccessDto.class);
        } catch (Exception e) {
            throw new CustomException(ALREADY_APPROVED);
        }
    }

    private JSONObject createPaymentRequestParams(String orderId, Long amount) {
        JSONObject params = new JSONObject();
        params.put("orderId", orderId);
        params.put("amount", amount);

        return params;
    }

    @Transactional
    public PaymentFailDto tossPaymentFail(String code, String message, String orderId) {
        handlePaymentFailure(orderId, message);

        return PaymentFailDto.builder()
                .errorCode(code)
                .errorMessage(message)
                .orderId(orderId)
                .build();
    }

    private void handlePaymentFailure(String orderId, String message) {
        Payment payment = findPaymentByOrderId(orderId);
        payment.markAsUnpaid();
        payment.recordFailReason(message);
    }

    private Payment findPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));
    }
}
