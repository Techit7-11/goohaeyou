package com.ll.goohaeyou.domain.payment.payment.service;

import com.ll.goohaeyou.domain.application.entity.Application;
import com.ll.goohaeyou.domain.application.service.ApplicationService;
import com.ll.goohaeyou.domain.payment.cashLog.service.CashLogService;
import com.ll.goohaeyou.domain.payment.payment.dto.cancel.PaymentCancelResDto;
import com.ll.goohaeyou.domain.payment.payment.entity.Payment;
import com.ll.goohaeyou.domain.payment.payment.entity.repository.PaymentRepository;
import com.ll.goohaeyou.global.exception.GoohaeyouException;
import com.ll.goohaeyou.global.exception.auth.AuthException;
import com.ll.goohaeyou.global.exception.payment.PaymentException;
import com.ll.goohaeyou.global.standard.base.util.TossPaymentUtil;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ll.goohaeyou.domain.application.entity.type.WageStatus.PAYMENT_CANCELLED;
import static com.ll.goohaeyou.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PaymentCancelService {
    private final PaymentRepository paymentRepository;
    private final TossPaymentUtil tossPaymentUtil;
    private final ApplicationService applicationService;
    private final CashLogService cashLogService;

    @Transactional
    public PaymentCancelResDto tossPaymentCancel(String username, String paymentKey, String cancelReason) {
        Payment payment = paymentRepository.findByPaymentKeyAndMemberUsername(paymentKey, username)
                .orElseThrow(PaymentException.PaymentNotFoundException::new);

        checkPaymentCancelable(username, payment);

        payment.cancelPayment(cancelReason);

        JSONObject params  = new JSONObject();
        params.put("cancelReason", cancelReason);

        UpdatePaymentAndApplication(payment, cancelReason);

        // TossPayments 결제 취소 API를 호출하고, 응답 값을 DTO 객체로 매핑
        PaymentCancelResDto paymentCancelResDto = tossPaymentUtil.sendPaymentCancelRequest(
                paymentKey, params, PaymentCancelResDto.class
        );

        cashLogService.createCashLogOnCancel(payment);

        return paymentCancelResDto;
    }

    private void checkPaymentCancelable(String username, Payment payment) {
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

    private void UpdatePaymentAndApplication(Payment payment, String cancelReason) {
        payment.cancelPayment(cancelReason);
        payment.markAsUnpaid();

        Application application = applicationService.findByIdAndValidate(payment.getApplicationId());
        application.updateWageStatus(PAYMENT_CANCELLED);
        application.setEarn(0);
        application.changeToNotCompleted();
    }
}