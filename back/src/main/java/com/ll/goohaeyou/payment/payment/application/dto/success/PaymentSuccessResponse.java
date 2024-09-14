package com.ll.goohaeyou.payment.payment.application.dto.success;

import jakarta.annotation.Nullable;

public record PaymentSuccessResponse(
        String paymentKey,
        String orderId,
        Long jobApplicationId,
        String orderName,
        String method,   // 카드(신한카드), 간편결제(네이버페이) 등
        int totalAmount,    // 총 결제금액
        String approvedAt,    // 결제승인 날짜, 시간
        @Nullable
        SuccessCardResponse card,
        @Nullable
        SuccessEasyPayResponse easyPay
) {
    public static PaymentSuccessResponse from(String paymentKey, String orderId, Long jobApplicationId, String orderName,
                                              String method, int totalAmount, String approvedAt,
                                              SuccessCardResponse card, SuccessEasyPayResponse easyPay) {
        return new PaymentSuccessResponse(
                paymentKey,
                orderId,
                jobApplicationId,
                orderName,
                method,
                totalAmount,
                approvedAt,
                card,
                easyPay
        );
    }
}
