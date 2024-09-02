package com.ll.goohaeyou.payment.payment.application.dto.request;

import com.ll.goohaeyou.payment.payment.domain.Payment;
import com.ll.goohaeyou.payment.payment.domain.type.PayStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class PaymentReqDto {
    @NotNull
    private PayStatus payStatus;
    @NotNull
    private Long amount;
    private String orderId;
    private String orderName;
    private Long jobApplicationId;

    public Payment toEntity() {
        return Payment.builder()
                .payStatus(payStatus.getDescription())
                .orderId(UUID.randomUUID().toString())
                .orderName(orderName)
                .jobApplicationId(jobApplicationId)
                .totalAmount(amount)
                .paid(false)
                .build();
    }
}
