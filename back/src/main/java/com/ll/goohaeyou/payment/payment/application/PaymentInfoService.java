package com.ll.goohaeyou.payment.payment.application;

import com.ll.goohaeyou.payment.payment.application.dto.PaymentInfoResponse;
import com.ll.goohaeyou.payment.payment.domain.policy.PaymentPolicy;
import com.ll.goohaeyou.payment.payment.domain.repository.PaymentRepository;
import com.ll.goohaeyou.payment.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentInfoService {
    private final PaymentRepository paymentRepository;
    private final PaymentPolicy paymentPolicy;

    public PaymentInfoResponse getValidPaymentInfo(String username, Long applicationId) {
        return paymentRepository.findByJobApplicationIdAndPaid(applicationId, true)
                .map(payment -> {
                    paymentPolicy.verifyPaymentUser(payment, username);

                    if (payment.isCanceled()) {
                        throw new PaymentException.AlreadyCanceledException();
                    }

                    return PaymentInfoResponse.from(payment);
                })
                .orElseThrow(PaymentException.PaymentInfoNotFoundException::new);
    }
}
