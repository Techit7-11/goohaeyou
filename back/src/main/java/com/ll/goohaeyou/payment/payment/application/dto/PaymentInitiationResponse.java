package com.ll.goohaeyou.payment.payment.application.dto;

import com.ll.goohaeyou.payment.payment.domain.util.OrderIdUtil;
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
    public static PaymentInitiationResponse of(PaymentRequest request, String successUrl, String failUrl,
                                               String username, Long userId) {
        return new PaymentInitiationResponse(
                request.amount(),
                OrderIdUtil.generateJobApplicationPaymentId(userId, request.jobApplicationId()),
                username,
                successUrl,
                failUrl
        );
    }
}
