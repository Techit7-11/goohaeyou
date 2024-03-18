package com.ll.gooHaeYu.domain.payment.payment.service;

import com.ll.gooHaeYu.domain.payment.payment.dto.PaymentDto;
import com.ll.gooHaeYu.domain.payment.payment.entity.Payment;
import com.ll.gooHaeYu.domain.payment.payment.repository.PaymentRepository;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ll.gooHaeYu.global.exception.ErrorCode.*;

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
                        throw new CustomException(ALREADY_CANCELED);
                    }

                    return buildPaymentDto(payment);
                })
                .orElseThrow(() -> new CustomException(PAYMENT_INFO_NOT_FOUND));
    }

    private void validatePayer(Payment payment, String username) {
        if (!payment.getMember().getUsername().equals(username)) {
            throw new CustomException(NOT_ABLE);
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
