package com.ll.gooHaeYu.domain.payment.payment.dto.request;

import com.ll.gooHaeYu.domain.payment.payment.entity.type.PayType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentResDto {
    @NotNull
    private PayType payType;

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
