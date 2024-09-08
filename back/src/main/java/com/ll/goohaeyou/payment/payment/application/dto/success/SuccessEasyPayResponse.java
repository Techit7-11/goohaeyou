package com.ll.goohaeyou.payment.payment.application.dto.success;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SuccessEasyPayResponse {
    private String provider;
    private int amount;
    private int discountAmount;
}
