package com.ll.goohaeyou.payment.payment.application.dto.cancel;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentCancelResDto {
    private int cancelAmount;
    private String transactionKey;
    private String canceledAt;
}
