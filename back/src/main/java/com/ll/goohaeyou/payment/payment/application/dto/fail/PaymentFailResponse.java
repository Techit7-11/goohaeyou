package com.ll.goohaeyou.payment.payment.application.dto.fail;

public record PaymentFailResponse(
        String errorCode,
        String errorMessage,
        String orderId
) {}
