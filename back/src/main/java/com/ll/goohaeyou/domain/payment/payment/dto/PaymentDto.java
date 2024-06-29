package com.ll.goohaeyou.domain.payment.payment.dto;

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
}
