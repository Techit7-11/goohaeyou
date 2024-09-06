package com.ll.goohaeyou.payment.payment.application.dto.cancel;

public record CancelPaymentResponse(
        int cancelAmount,
        String transactionKey,
        String canceledAt
) {}
