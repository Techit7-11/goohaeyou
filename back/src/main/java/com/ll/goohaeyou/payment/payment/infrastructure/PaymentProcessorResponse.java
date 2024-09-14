package com.ll.goohaeyou.payment.payment.infrastructure;

import com.ll.goohaeyou.payment.payment.application.dto.success.SuccessCardResponse;
import com.ll.goohaeyou.payment.payment.application.dto.success.SuccessEasyPayResponse;
import lombok.Getter;

@Getter
public class PaymentProcessorResponse {
    private String paymentKey;
    private String orderId;
    private String orderName;
    private String method;
    private int totalAmount;
    private String approvedAt;
    private SuccessCardResponse card;
    private SuccessEasyPayResponse easyPay;
}
