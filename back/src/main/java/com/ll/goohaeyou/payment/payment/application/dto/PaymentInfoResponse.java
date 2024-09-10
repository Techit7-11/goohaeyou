package com.ll.goohaeyou.payment.payment.application.dto;

import com.ll.goohaeyou.payment.payment.domain.Payment;

public record PaymentInfoResponse(
        String paymentKey,
        Long totalAmount,
        String orderName,
        boolean paid,
        boolean canceled,
        Long jobApplicationId,
        String payStatus
) {
    public static PaymentInfoResponse from(Payment payment) {
        return new PaymentInfoResponse(
                payment.getPaymentKey(),
                payment.getTotalAmount(),
                payment.getOrderName(),
                payment.isPaid(),
                payment.isCanceled(),
                payment.getJobApplicationId(),
                payment.getPayStatus()
        );
    }
}
