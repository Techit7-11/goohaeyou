package com.ll.goohaeyou.payment.payment.application.dto;

import com.ll.goohaeyou.payment.payment.domain.entity.Payment;
import jakarta.validation.constraints.NotNull;

public record PaymentResponse(
        @NotNull
        Long amount,
        @NotNull
        String orderId,
        @NotNull
        String orderName,
        @NotNull
        String payer,
        @NotNull
        String successUrl,
        @NotNull
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
