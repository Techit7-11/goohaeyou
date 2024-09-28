package com.ll.goohaeyou.payment.payment.application;

import com.ll.goohaeyou.global.policy.ThrottlePolicy;
import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobApplication.domain.service.JobApplicationDomainService;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.member.member.domain.service.MemberDomainService;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentInitiationResponse;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentRequest;
import com.ll.goohaeyou.payment.payment.application.dto.success.PaymentSuccessResponse;
import com.ll.goohaeyou.payment.payment.domain.entity.Payment;
import com.ll.goohaeyou.payment.payment.domain.policy.PaymentPolicy;
import com.ll.goohaeyou.payment.payment.domain.service.PaymentDomainService;
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
    public PaymentInitiationResponse requestTossPayment(PaymentRequest request, String username) {
        throttlePolicy.checkThrottle(username, System.currentTimeMillis());

        Member member = memberDomainService.getByUsername(username);

        return paymentDomainService.preparePaymentResponse(request, member.getUsername(), member.getId());
    }

    @Transactional
    public PaymentSuccessResponse tossPaymentSuccess(String paymentKey, String orderId, Long amount) {
        PaymentSuccessResponse successResponse = paymentDomainService.requestPaymentAccept(paymentKey, orderId, amount);

        Payment payment = paymentDomainService.create(successResponse);

        JobApplication jobApplication = jobApplicationDomainService.getById(payment.getJobApplicationId());

        jobApplicationDomainService.updateStatusByPaymentSuccess(jobApplication, amount);

        return successResponse;
    }

    @Transactional
    public void cancelPendingPayment(String username, Long jobApplicationId) {
        Payment payment = paymentDomainService.getUnpaidByJobApplicationId(jobApplicationId);

        paymentPolicy.validatePendingPaymentCancelable(username, payment);

        paymentDomainService.delete(payment);
    }
}
