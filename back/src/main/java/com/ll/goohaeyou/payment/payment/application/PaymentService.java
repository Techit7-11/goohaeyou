package com.ll.goohaeyou.payment.payment.application;

import com.ll.goohaeyou.global.standard.retryOnOptimisticLock.RetryOnOptimisticLock;
import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobApplication.domain.service.JobApplicationDomainService;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.member.member.domain.service.MemberDomainService;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentRequest;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentResponse;
import com.ll.goohaeyou.payment.payment.application.dto.fail.PaymentFailResponse;
import com.ll.goohaeyou.payment.payment.application.dto.success.PaymentSuccessResponse;
import com.ll.goohaeyou.payment.payment.domain.PaymentDomainService;
import com.ll.goohaeyou.payment.payment.domain.entity.Payment;
import com.ll.goohaeyou.payment.payment.domain.policy.PaymentPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentPolicy paymentPolicy;
    private final MemberDomainService memberDomainService;
    private final JobApplicationDomainService jobApplicationDomainService;
    private final PaymentDomainService paymentDomainService;

    @Transactional
    @RetryOnOptimisticLock(attempts = 2, backoff = 500L)
    public PaymentResponse requestTossPayment(PaymentRequest request, String username) {
        Member member = memberDomainService.getByUsername(username);

        Payment payment = paymentDomainService.create(request, member);

        return paymentDomainService.preparePaymentResponse(payment);
    }

    @Transactional
    public PaymentSuccessResponse tossPaymentSuccess(String paymentKey, String orderId, Long amount) {
        Payment payment = paymentDomainService.getByOrderId(orderId);

        paymentPolicy.verifyPaymentAmount(payment, amount);

        PaymentSuccessResponse successResponse = paymentDomainService.requestPaymentAccept(paymentKey, orderId, amount);

        paymentDomainService.update(payment, successResponse);

        JobApplication jobApplication = jobApplicationDomainService.getById(payment.getJobApplicationId());

        jobApplicationDomainService.updateStatusByPaymentSuccess(jobApplication, amount);

        return successResponse;
    }
    @Transactional
    public PaymentFailResponse tossPaymentFail(String code, String message, String orderId) {
        Payment payment = paymentDomainService.getByOrderId(orderId);
        payment.markAsUnpaid();

        paymentDomainService.delete(payment);

        return new PaymentFailResponse(code, message, orderId);
    }
}
