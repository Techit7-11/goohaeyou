package com.ll.goohaeyou.payment.payment.application.dto.cancel;

public record CancelPaymentRequest(
        int cancelAmount,
        String transactionKey,
        String canceledAt
) {}