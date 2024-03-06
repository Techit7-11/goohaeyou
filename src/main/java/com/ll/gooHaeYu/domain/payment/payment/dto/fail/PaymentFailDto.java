package com.ll.gooHaeYu.domain.payment.payment.dto.fail;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentFailDto {
    private String errorCode;

    private String errorMessage;

    private String orderId;
}
