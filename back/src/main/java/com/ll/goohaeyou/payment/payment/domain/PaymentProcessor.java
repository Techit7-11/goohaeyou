package com.ll.goohaeyou.payment.payment.domain;

import net.minidev.json.JSONObject;

public interface PaymentProcessor {
    <T> T sendPaymentRequest(String paymentKey, JSONObject params, Class<T> responseType);
    <T> T sendPaymentCancelRequest(String paymentKey, JSONObject params, Class<T> responseType);
}
