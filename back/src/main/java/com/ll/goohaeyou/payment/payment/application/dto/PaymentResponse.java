package com.ll.goohaeyou.payment.payment.application.dto;

import com.ll.goohaeyou.payment.payment.domain.entity.Payment;

public record PaymentResponse(
        Long amount,
        String orderId,
        String orderName,
        String payer,
        String successUrl,
        String failUrl
) {
    public static PaymentResponse from(Payment payment, String successUrl, String failUrl) {
        return new PaymentResponse(
                payment.getTotalAmount(),
                payment.getOrderId(),
                payment.getOrderName(),
                payment.getMember().getUsername(),
                successUrl,
                failUrl
        );
    }
}
