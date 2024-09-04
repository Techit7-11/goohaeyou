package com.ll.goohaeyou.payment.payment.application;

import com.ll.goohaeyou.global.config.TossPaymentsConfig;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.standard.base.util.TossPaymentUtil;
import com.ll.goohaeyou.global.standard.retryOnOptimisticLock.RetryOnOptimisticLock;
import com.ll.goohaeyou.jobApplication.application.JobApplicationService;
import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
import com.ll.goohaeyou.member.member.exception.MemberException;
import com.ll.goohaeyou.payment.cashLog.application.CashLogService;
import com.ll.goohaeyou.payment.payment.application.dto.fail.PaymentFailDto;
import com.ll.goohaeyou.payment.payment.application.dto.request.PaymentReqDto;
import com.ll.goohaeyou.payment.payment.application.dto.request.PaymentResDto;
import com.ll.goohaeyou.payment.payment.application.dto.success.PaymentSuccessDto;
import com.ll.goohaeyou.payment.payment.domain.Payment;
import com.ll.goohaeyou.payment.payment.domain.repository.PaymentRepository;
import com.ll.goohaeyou.payment.payment.domain.type.PayStatus;
import com.ll.goohaeyou.payment.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final TossPaymentsConfig tossPaymentsConfig;
    private final MemberRepository memberRepository;
    private final JobApplicationService jobApplicationService;
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
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);

        Payment payment = Payment.from(paymentReqDto);
        payment.updatePayer(member);

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

        jobApplicationService.updateJobApplicationOnPaymentSuccess(payment.getJobApplicationId(), amount);

        cashLogService.createCashLogOnPaid(successDto, payment);

        return successDto;
    }

    public Payment verifyPayment(String orderId, Long amount) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(MemberException.MemberNotFoundException::new);

        if (!payment.getTotalAmount().equals(amount)) {
            throw new PaymentException.PaymentAmountMismatchException();
        }

        return payment;
    }

    @Transactional
    public PaymentSuccessDto requestPaymentAccept(String paymentKey, String orderId, Long amount) {
        JSONObject params = createPaymentRequestParams(orderId, amount);

        PaymentSuccessDto paymentSuccessDto = tossPaymentUtil.sendPaymentRequest(
                paymentKey, params, PaymentSuccessDto.class);

        paymentSuccessDto.setJobApplicationId(findPaymentByOrderId(orderId).getJobApplicationId());

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
                .orElseThrow(PaymentException.PaymentNotFoundException::new);
    }
}
