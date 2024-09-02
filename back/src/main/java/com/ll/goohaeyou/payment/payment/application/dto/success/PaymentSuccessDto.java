package com.ll.goohaeyou.payment.payment.application.dto.success;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentSuccessDto {
    private String paymentKey;
    private String orderId;
    private Long jobApplicationId;
    private String orderName;
    private String method;   // 카드(신한카드), 간편결제(네이버페이) 등
    private int totalAmount;    // 총 결제금액
    private String approvedAt;    // 결제승인 날짜,시간
    private SuccessCardDto card;
    private SuccessEasyPayDto easyPay;
}
