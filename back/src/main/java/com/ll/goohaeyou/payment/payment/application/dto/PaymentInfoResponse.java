package com.ll.goohaeyou.payment.payment.application.dto;

import com.ll.goohaeyou.payment.payment.domain.entity.Payment;

public record PaymentInfoResponse(
        String paymentKey,
        int totalAmount,
        String orderName,
        boolean paid,
        boolean canceled,
        Long jobApplicationId,
        String payMethod
) {
    public static PaymentInfoResponse from(Payment payment) {
        return new PaymentInfoResponse(
                payment.getPaymentKey(),
                payment.getTotalAmount(),
                payment.getOrderName(),
                payment.isPaid(),
                payment.isCanceled(),
                payment.getJobApplicationId(),
                payment.getPayMethod()
        );
    }
}
