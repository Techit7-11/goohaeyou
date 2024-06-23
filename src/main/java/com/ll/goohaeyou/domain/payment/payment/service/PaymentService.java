package com.ll.goohaeyou.domain.payment.payment.service;

import com.ll.goohaeyou.domain.application.application.service.ApplicationService;
import com.ll.goohaeyou.domain.member.member.service.MemberService;
import com.ll.goohaeyou.domain.payment.cashLog.service.CashLogService;
import com.ll.goohaeyou.domain.payment.payment.dto.fail.PaymentFailDto;
import com.ll.goohaeyou.domain.payment.payment.dto.request.PaymentReqDto;
import com.ll.goohaeyou.domain.payment.payment.dto.request.PaymentResDto;
import com.ll.goohaeyou.domain.payment.payment.dto.success.PaymentSuccessDto;
import com.ll.goohaeyou.domain.payment.payment.entity.Payment;
import com.ll.goohaeyou.domain.payment.payment.entity.type.PayStatus;
import com.ll.goohaeyou.domain.payment.payment.repository.PaymentRepository;
import com.ll.goohaeyou.global.config.TossPaymentsConfig;
import com.ll.goohaeyou.global.exception.CustomException;
import com.ll.goohaeyou.standard.base.util.TossPaymentUtil;
import com.ll.goohaeyou.standard.retryOnOptimisticLock.RetryOnOptimisticLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ll.goohaeyou.global.exception.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final TossPaymentsConfig tossPaymentsConfig;
    private final MemberService memberService;
    private final ApplicationService applicationService;
    private final TossPaymentUtil tossPaymentUtil;
    private final CashLogService cashLogService;

    @Transactional
    @RetryOnOptimisticLock(attempts = 2, backoff = 500L)
    public PaymentResDto requestTossPayment(PaymentReqDto paymentReqDto, String username) {
        Payment payment = createPaymentEntity(paymentReqDto, username);

        Payment savedPayment = paymentRepository.save(payment);
        PaymentResDto paymentResDto = savedPayment.toPaymentRespDto();
        setRedirectUrls(paymentResDto);

        return paymentResDto;
    }

    private Payment createPaymentEntity(PaymentReqDto paymentReqDto, String username) {
        Payment payment = paymentReqDto.toEntity();
        payment.updatePayer(memberService.getMember(username));

        return payment;
    }

    private void setRedirectUrls(PaymentResDto paymentResDto) {
        paymentResDto.setSuccessUrl(tossPaymentsConfig.getSuccessUrl());
        paymentResDto.setFailUrl(tossPaymentsConfig.getFailUrl());
    }

    @Transactional
    public PaymentSuccessDto tossPaymentSuccess(String paymentKey, String orderId, Long amount) {
        Payment payment = verifyPayment(orderId, amount);
        PaymentSuccessDto successDto = requestPaymentAccept(paymentKey, orderId, amount);

        updatePayment(payment, successDto);

        applicationService.updateApplicationOnPaymentSuccess(payment.getApplicationId(), amount);

        cashLogService.createCashLogOnPaid(successDto, payment);

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

    @Transactional
    public PaymentSuccessDto requestPaymentAccept(String paymentKey, String orderId, Long amount) {
        JSONObject params = createPaymentRequestParams(orderId, amount);

        PaymentSuccessDto paymentSuccessDto = tossPaymentUtil.sendPaymentRequest(
                paymentKey, params, PaymentSuccessDto.class);

        paymentSuccessDto.setApplicationId(findPaymentByOrderId(orderId).getApplicationId());

        return paymentSuccessDto;
    }

    private JSONObject createPaymentRequestParams(String orderId, Long amount) {
        JSONObject params = new JSONObject();
        params.put("orderId", orderId);
        params.put("amount", amount);

        return params;
    }

    private void updatePayment(Payment payment, PaymentSuccessDto successDto) {
        payment.updatePaymentKey(successDto.getPaymentKey());
        payment.markAsPaid();
        updatePayStatusByPayment(payment, successDto.getMethod());
    }

    private void updatePayStatusByPayment(Payment payment, String method) {
        PayStatus payStatus = PayStatus.findByMethod(method);
        payment.updatePayStatus(payStatus);
    }

    @Transactional
    public PaymentFailDto tossPaymentFail(String code, String message, String orderId) {
        handlePaymentFailure(orderId, message);

        Payment payment = findPaymentByOrderId(orderId);
        paymentRepository.delete(payment);

        return PaymentFailDto.builder()
                .errorCode(code)
                .errorMessage(message)
                .orderId(orderId)
                .build();
    }

    private void handlePaymentFailure(String orderId, String message) {
        Payment payment = findPaymentByOrderId(orderId);
        payment.markAsUnpaid();
    }

    private Payment findPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));
    }
}
