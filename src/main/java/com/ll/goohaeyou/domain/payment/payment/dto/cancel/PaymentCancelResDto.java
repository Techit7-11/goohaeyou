package com.ll.goohaeyou.domain.payment.payment.dto.cancel;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentCancelResDto {
    private int cancelAmount;     // 취소한 금액
    private String transactionKey;    // 취소건의 키 값
    private String canceledAt;     // 결제 취소 날짜
}
