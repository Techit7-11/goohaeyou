package com.ll.goohaeyou.payment.payment.application;

import com.ll.goohaeyou.payment.payment.application.dto.PaymentInfoResponse;
import com.ll.goohaeyou.payment.payment.domain.Payment;
import com.ll.goohaeyou.payment.payment.domain.repository.PaymentRepository;
import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.payment.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentInfoService {
    private final PaymentRepository paymentRepository;

    public PaymentInfoResponse getValidPaymentInfo(String username, Long applicationId) {
        return paymentRepository.findByJobApplicationIdAndPaid(applicationId, true)
                .map(payment -> {
                    validatePayer(payment, username);

                    if (payment.isCanceled()) {
                        throw new PaymentException.AlreadyCanceledException();
                    }

                    return PaymentInfoResponse.from(payment);
                })
                .orElseThrow(PaymentException.PaymentInfoNotFoundException::new);
    }

    private void validatePayer(Payment payment, String username) {
        if (!payment.getMember().getUsername().equals(username)) {
            throw new AuthException.NotAuthorizedException();
        }
    }
}
