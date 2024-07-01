package com.ll.gooHaeYu.domain.payment.payment.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PayTypeFee {     // 부가세 10% 별도
    CARD("카드", 3.3, 0),
    EASY_PAY("간편결제", 3.3, 0);

    private final String typeName;    // 결제수단
    private final double feePercentage;    // 수수료율
    private final int transactionFee;    // 건당 수수료
}
