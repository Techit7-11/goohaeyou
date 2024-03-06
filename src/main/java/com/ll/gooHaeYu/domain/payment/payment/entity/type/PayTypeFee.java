package com.ll.gooHaeYu.domain.payment.payment.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PayTypeFee {     // 부가세 10% 별도
    CARD("카드", 3.3, 0),
    EASY_PAYMENT("간편결제", 3.3, 0),
    VIRTUAL_ACCOUNT("가상계좌", 0.0, 400),
    ACCOUNT_TRANSFER("계좌이체", 2.0, 0),
    BRAND_PAY("브랜드페이", 4.3, 0);

    private final String typeName;    // 결제수단
    private final double feePercentage;    // 수수료율
    private final int transactionFee;    // 건당 수수료
}
