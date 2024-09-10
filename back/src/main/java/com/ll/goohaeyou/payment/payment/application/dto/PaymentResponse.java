package com.ll.goohaeyou.payment.payment.application.dto;

import com.ll.goohaeyou.payment.payment.domain.type.PayStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentResponse {
    @NotNull
    private PayStatus payStatus;
    @NotNull
    private Long amount;
    @NotBlank
    private String orderId;
    @NotBlank
    private String orderName;
    @NotBlank
    private String payer;
    private String successUrl;
    private String failUrl;
    private String failReason;
    private boolean canceled;
    private String cancelReason;
}
