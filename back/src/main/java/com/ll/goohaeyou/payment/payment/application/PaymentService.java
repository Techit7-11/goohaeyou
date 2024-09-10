package com.ll.goohaeyou.payment.payment.application;

import com.ll.goohaeyou.global.config.TossPaymentsConfig;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.standard.retryOnOptimisticLock.RetryOnOptimisticLock;
import com.ll.goohaeyou.jobApplication.domain.JobApplication;
import com.ll.goohaeyou.jobApplication.domain.repository.JobApplicationRepository;
import com.ll.goohaeyou.jobApplication.domain.type.WageStatus;
import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
import com.ll.goohaeyou.member.member.exception.MemberException;
import com.ll.goohaeyou.payment.payment.application.dto.fail.PaymentFailResponse;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentRequest;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentResponse;
import com.ll.goohaeyou.payment.payment.application.dto.success.PaymentSuccessResponse;
import com.ll.goohaeyou.payment.payment.domain.Payment;
import com.ll.goohaeyou.payment.payment.domain.repository.PaymentRepository;
import com.ll.goohaeyou.payment.payment.domain.type.PayStatus;
import com.ll.goohaeyou.payment.payment.exception.PaymentException;
import com.ll.goohaeyou.payment.payment.infrastructure.TossPaymentUtil;
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
    private final TossPaymentUtil tossPaymentUtil;
    private final JobApplicationRepository jobApplicationRepository;

    @Transactional
    @RetryOnOptimisticLock(attempts = 2, backoff = 500L)
    public PaymentResponse requestTossPayment(PaymentRequest request, String username) {
        Payment payment = createPaymentEntity(request, username);

        Payment savedPayment = paymentRepository.save(payment);
        PaymentResponse response = savedPayment.toPaymentRespDto();
        setRedirectUrls(response);

        return response;
    }

    private Payment createPaymentEntity(PaymentRequest request, String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);

        Payment payment = Payment.create(request);
        payment.updatePayer(member);

        return payment;
    }

    private void setRedirectUrls(PaymentResponse response) {
        response.setSuccessUrl(tossPaymentsConfig.getSuccessUrl());
        response.setFailUrl(tossPaymentsConfig.getFailUrl());
    }

    @Transactional
    public PaymentSuccessResponse tossPaymentSuccess(String paymentKey, String orderId, Long amount) {
        Payment payment = verifyPayment(orderId, amount);
        PaymentSuccessResponse successDto = requestPaymentAccept(paymentKey, orderId, amount);

        updatePayment(payment, successDto);

        JobApplication jobApplication = jobApplicationRepository.findById(payment.getJobApplicationId())
                        .orElseThrow(EntityNotFoundException.JobApplicationNotExistsException::new);

        jobApplication.updateWageStatus(WageStatus.PAYMENT_COMPLETED);
        jobApplication.updateEarn(Math.toIntExact(amount));

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
    public PaymentSuccessResponse requestPaymentAccept(String paymentKey, String orderId, Long amount) {
        JSONObject params = createPaymentRequestParams(orderId, amount);

        PaymentSuccessResponse paymentSuccessResponse = tossPaymentUtil.sendPaymentRequest(
                paymentKey, params, PaymentSuccessResponse.class);

        paymentSuccessResponse.setJobApplicationId(findPaymentByOrderId(orderId).getJobApplicationId());

        return paymentSuccessResponse;
    }

    private JSONObject createPaymentRequestParams(String orderId, Long amount) {
        JSONObject params = new JSONObject();
        params.put("orderId", orderId);
        params.put("amount", amount);

        return params;
    }

    private void updatePayment(Payment payment, PaymentSuccessResponse successDto) {
        payment.updatePaymentKey(successDto.getPaymentKey());
        payment.markAsPaid();
        updatePayStatusByPayment(payment, successDto.getMethod());
    }

    private void updatePayStatusByPayment(Payment payment, String method) {
        PayStatus payStatus = PayStatus.findByMethod(method);
        payment.updatePayStatus(payStatus);
    }

    @Transactional
    public PaymentFailResponse tossPaymentFail(String code, String message, String orderId) {
        handlePaymentFailure(orderId, message);

        Payment payment = findPaymentByOrderId(orderId);
        paymentRepository.delete(payment);

        return new PaymentFailResponse(code, message, orderId);
    }

    private void handlePaymentFailure(String orderId, String message) {
        Payment payment = findPaymentByOrderId(orderId);
        payment.markAsUnpaid();
    }

    private Payment findPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(EntityNotFoundException.PaymentNotFoundException::new);
    }
}
