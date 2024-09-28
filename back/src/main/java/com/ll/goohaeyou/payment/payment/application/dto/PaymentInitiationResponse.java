package com.ll.goohaeyou.payment.payment.application.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentInitiationResponse(
        @NotNull
        Long amount,
        @NotNull
        String orderId,
        @NotNull
        String payer,
        @NotNull
        String successUrl,
        @NotNull
        String failUrl
) {
    public static PaymentInitiationResponse of(PaymentRequest request, String orderId, String successUrl, String failUrl,
                                               String username) {
        return new PaymentInitiationResponse(
                request.amount(),
                orderId,
                username,
                successUrl,
                failUrl
        );
    }
}
