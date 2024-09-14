package com.ll.goohaeyou.payment.payment.application;

import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobApplication.domain.service.JobApplicationDomainService;
import com.ll.goohaeyou.payment.payment.application.dto.cancel.PaymentCancelResponse;
import com.ll.goohaeyou.payment.payment.domain.PaymentDomainService;
import com.ll.goohaeyou.payment.payment.domain.entity.Payment;
import com.ll.goohaeyou.payment.payment.domain.policy.PaymentPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentCancelService {
    private final PaymentPolicy paymentPolicy;
    private final JobApplicationDomainService jobApplicationDomainService;
    private final PaymentDomainService paymentDomainService;

    @Transactional
    public PaymentCancelResponse tossPaymentCancel(String username, String paymentKey, String cancelReason) {
        Payment payment = paymentDomainService.getByPaymentKeyAndUsername(paymentKey, username);

        paymentPolicy.verifyPaymentCancelable(payment, username);

        paymentDomainService.cancel(payment, cancelReason);

        JobApplication jobApplication = jobApplicationDomainService.getById(payment.getJobApplicationId());

        jobApplicationDomainService.updateOnPaymentCancel(jobApplication);

        return paymentDomainService.sendPaymentCancelRequest(paymentKey, cancelReason);
    }
}
