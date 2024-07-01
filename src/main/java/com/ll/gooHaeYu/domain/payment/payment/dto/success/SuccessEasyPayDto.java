package com.ll.gooHaeYu.domain.payment.payment.dto.success;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SuccessEasyPayDto {
    private String provider;
    private int amount;
    private int discountAmount;
}
