package com.ll.goohaeyou.payment.payment.application;

import com.ll.goohaeyou.payment.payment.application.dto.PaymentDto;
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

    public PaymentDto getValidPayment(String username, Long applicationId) {
        return paymentRepository.findByJobApplicationIdAndPaid(applicationId, true)
                .map(payment -> {
                    validatePayer(payment, username);

                    if (payment.isCanceled()) {
                        throw new PaymentException.AlreadyCanceledException();
                    }

                    return buildPaymentDto(payment);
                })
                .orElseThrow(PaymentException.PaymentInfoNotFoundException::new);
    }

    private void validatePayer(Payment payment, String username) {
        if (!payment.getMember().getUsername().equals(username)) {
            throw new AuthException.NotAuthorizedException();
        }
    }

    private PaymentDto buildPaymentDto(Payment payment) {
        return PaymentDto.builder()
                .paymentKey(payment.getPaymentKey())
                .canceled(payment.isCanceled())
                .paid(payment.isPaid())
                .totalAmount(payment.getTotalAmount())
                .jobApplicationId(payment.getJobApplicationId())
                .orderName(payment.getOrderName())
                .payStatus(payment.getPayStatus())
                .build();
    }
}
