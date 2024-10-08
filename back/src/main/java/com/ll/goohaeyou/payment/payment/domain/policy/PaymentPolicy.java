package com.ll.goohaeyou.payment.payment.domain.policy;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.global.exception.GoohaeyouException;
import com.ll.goohaeyou.payment.payment.domain.entity.Payment;
import com.ll.goohaeyou.payment.payment.exception.PaymentException;
import org.springframework.stereotype.Component;

import static com.ll.goohaeyou.global.exception.ErrorCode.BAD_REQUEST;

@Component
public class PaymentPolicy {

    // 결제 취소 가능 여부 검증
    public void verifyPaymentCancelable(Payment payment, String username) {
        if (payment.isCanceled()) {
            throw new PaymentException.AlreadyCanceledException();
        }
        if (!payment.getMember().getUsername().equals(username)) {
            throw new AuthException.NotAuthorizedException();
        }
        if (!payment.isPaid()) {
            throw new GoohaeyouException(BAD_REQUEST);
        }
    }

    // 결제 사용자 검증
    public void verifyPaymentUser(Payment payment, String username) {
        if (!payment.getMember().getUsername().equals(username)) {
            throw new AuthException.NotAuthorizedException();
        }
    }
}
