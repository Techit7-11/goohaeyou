package com.ll.gooHaeYu.domain.payment.payment.dto;

import com.ll.gooHaeYu.domain.payment.payment.entity.Payment;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaymentDto {
    private String paymentKey;

    private Long totalAmount;

    private String orderName;

    private boolean paid;

    private boolean canceled;

    private Long applicationId;

    private String payStatus;

    public static PaymentDto fromEntity(Payment payment) {
        return PaymentDto.builder()
                .paymentKey(payment.getPaymentKey())
                .canceled(payment.isCanceled())
                .paid(payment.isPaid())
                .totalAmount(payment.getTotalAmount())
                .applicationId(payment.getApplicationId())
                .orderName(payment.getOrderName())
                .payStatus(payment.getPayStatus())
                .build();
    }
}
