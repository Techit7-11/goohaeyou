package com.ll.goohaeyou.domain.payment.payment.dto.cancel;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentCancelResDto {
    private int cancelAmount;
    private String transactionKey;
    private String canceledAt;
}
