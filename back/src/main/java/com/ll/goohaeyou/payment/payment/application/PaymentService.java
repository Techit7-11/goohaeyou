package com.ll.goohaeyou.payment.payment.application;

import com.ll.goohaeyou.global.policy.ThrottlePolicy;
import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobApplication.domain.service.JobApplicationDomainService;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.member.member.domain.service.MemberDomainService;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentRequest;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentResponse;
import com.ll.goohaeyou.payment.payment.application.dto.fail.PaymentFailResponse;
import com.ll.goohaeyou.payment.payment.application.dto.success.PaymentSuccessResponse;
import com.ll.goohaeyou.payment.payment.domain.service.PaymentDomainService;
import com.ll.goohaeyou.payment.payment.domain.entity.Payment;
import com.ll.goohaeyou.payment.payment.domain.policy.PaymentPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentPolicy paymentPolicy;
    private final MemberDomainService memberDomainService;
    private final JobApplicationDomainService jobApplicationDomainService;
    private final PaymentDomainService paymentDomainService;
    private final ThrottlePolicy throttlePolicy;

    @Transactional
    public PaymentResponse requestTossPayment(PaymentRequest request, String username) {
        throttlePolicy.checkThrottle(username, System.currentTimeMillis());

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

        paymentDomainService.delete(payment);

        return new PaymentFailResponse(code, message, orderId);
    }

    @Transactional
    public void cancelPendingPayment(String username, Long jobApplicationId) {
        Payment payment = paymentDomainService.getUnpaidByJobApplicationId(jobApplicationId);

        paymentPolicy.validatePendingPaymentCancelable(username, payment);

        paymentDomainService.delete(payment);
    }
}
