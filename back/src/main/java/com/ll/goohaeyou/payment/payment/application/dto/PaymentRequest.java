package com.ll.goohaeyou.payment.payment.application.dto;

import com.ll.goohaeyou.payment.payment.domain.type.PayStatus;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull
        PayStatus payStatus,

        @NotNull
        Long amount,

        String orderName,
        Long jobApplicationId
) {}
