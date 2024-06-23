package com.ll.goohaeyou.domain.payment.payment.service;

import com.ll.goohaeyou.domain.application.application.entity.Application;
import com.ll.goohaeyou.domain.application.application.service.ApplicationService;
import com.ll.goohaeyou.domain.payment.cashLog.service.CashLogService;
import com.ll.goohaeyou.domain.payment.payment.dto.cancel.PaymentCancelResDto;
import com.ll.goohaeyou.domain.payment.payment.entity.Payment;
import com.ll.goohaeyou.domain.payment.payment.repository.PaymentRepository;
import com.ll.goohaeyou.global.exception.CustomException;
import com.ll.goohaeyou.standard.base.util.TossPaymentUtil;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import static com.ll.goohaeyou.domain.application.application.entity.type.WageStatus.PAYMENT_CANCELLED;
import static com.ll.goohaeyou.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PaymentCancelService {
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    private final TossPaymentUtil tossPaymentUtil;
    private final ApplicationService applicationService;
    private final CashLogService cashLogService;

    @Transactional
    public PaymentCancelResDto tossPaymentCancel(String username, String paymentKey, String cancelReason) {
        Payment payment = paymentRepository.findByPaymentKeyAndMemberUsername(paymentKey, username)
                .orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));

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
            throw new CustomException(ALREADY_CANCELED);
        }
        if (!payment.getMember().getUsername().equals(username)) {
            throw new CustomException(NOT_ABLE);
        }
        if (!payment.isPaid()) {
            throw new CustomException(BAD_REQUEST);
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
