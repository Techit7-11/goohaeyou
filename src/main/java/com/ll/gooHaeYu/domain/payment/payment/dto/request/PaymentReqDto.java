package com.ll.gooHaeYu.domain.payment.payment.dto.request;

import com.ll.gooHaeYu.domain.payment.payment.entity.Payment;
import com.ll.gooHaeYu.domain.payment.payment.entity.type.PayStatus;
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

    private Long applicationId;

    public Payment toEntity() {
        return Payment.builder()
                .payStatus(payStatus.getDescription())
                .orderId(UUID.randomUUID().toString())
                .orderName(orderName)
                .applicationId(applicationId)
                .totalAmount(amount)
                .paid(false)
                .build();
    }
}
