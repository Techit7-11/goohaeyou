package com.ll.goohaeyou.payment.payment.application;

import com.ll.goohaeyou.payment.payment.application.dto.PaymentInfoResponse;
import com.ll.goohaeyou.payment.payment.domain.service.PaymentDomainService;
import com.ll.goohaeyou.payment.payment.domain.entity.Payment;
import com.ll.goohaeyou.payment.payment.domain.policy.PaymentPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentInfoService {
    private final PaymentDomainService paymentDomainService;
    private final PaymentPolicy paymentPolicy;

    public PaymentInfoResponse getPaymentInfo(String username, Long JobApplicationId) {
        Payment payment = paymentDomainService.getPaidByJobApplicationId(JobApplicationId);

        paymentPolicy.verifyPaymentUser(payment, username);

        return PaymentInfoResponse.from(payment);
    }
}
