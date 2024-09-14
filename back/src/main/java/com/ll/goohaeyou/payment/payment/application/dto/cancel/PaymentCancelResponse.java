package com.ll.goohaeyou.payment.payment.application.dto.cancel;

public record PaymentCancelResponse(
        int cancelAmount,
        String transactionKey,
        String canceledAt
) {}
