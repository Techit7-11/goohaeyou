package com.ll.goohaeyou.payment.payment.application.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull
        Long amount,
        @NotNull
        String orderName,
        @NotNull
        Long jobApplicationId
) {}
