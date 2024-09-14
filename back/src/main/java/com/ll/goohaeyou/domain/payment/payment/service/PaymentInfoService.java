package com.ll.goohaeyou.domain.payment.payment.service;

import com.ll.goohaeyou.domain.payment.payment.dto.PaymentDto;
import com.ll.goohaeyou.domain.payment.payment.entity.Payment;
import com.ll.goohaeyou.domain.payment.payment.entity.repository.PaymentRepository;
import com.ll.goohaeyou.global.exception.auth.AuthException;
import com.ll.goohaeyou.global.exception.payment.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentInfoService {
    private final PaymentRepository paymentRepository;

    public PaymentDto getValidPayment(String username, Long applicationId) {
        return paymentRepository.findByApplicationIdAndPaid(applicationId, true)
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
                .applicationId(payment.getApplicationId())
                .orderName(payment.getOrderName())
                .payStatus(payment.getPayStatus())
                .build();
    }
}
