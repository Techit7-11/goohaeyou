package com.ll.goohaeyou.payment.payment.application.dto.request;

import com.ll.goohaeyou.payment.payment.domain.type.PayStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentReqDto {
    @NotNull
    private PayStatus payStatus;

    @NotNull
    private Long amount;

    private String orderName;
    private Long jobApplicationId;
}
