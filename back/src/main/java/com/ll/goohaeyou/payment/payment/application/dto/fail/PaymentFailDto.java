package com.ll.goohaeyou.payment.payment.application.dto.fail;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentFailDto {
    private String errorCode;
    private String errorMessage;
    private String orderId;
}
