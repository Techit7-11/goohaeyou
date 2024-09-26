package com.ll.goohaeyou.payment.cashLog.application;

import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.payment.cashLog.domain.service.CashLogDomainService;
import com.ll.goohaeyou.payment.payment.application.dto.success.PaymentSuccessResponse;
import com.ll.goohaeyou.payment.payment.domain.entity.Payment;
import com.ll.goohaeyou.payment.payment.domain.service.PaymentDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CashLogService {
    private final CashLogDomainService cashLogDomainService;
    private final PaymentDomainService paymentDomainService;

    @Transactional
    public void createCashLogOnSettled(JobApplication jobApplication) {
        cashLogDomainService.createSettlementLog(jobApplication);
    }

    @Transactional
    public void createCashLogOnPaid(PaymentSuccessResponse successDto) {
        Payment payment = paymentDomainService.getByPaymentKey(successDto.paymentKey());

        cashLogDomainService.createPaymentLog(payment, successDto);
    }

    @Transactional
    public void createCashLogOnCancel(String paymentKey) {
        Payment payment = paymentDomainService.getByPaymentKey(paymentKey);

        cashLogDomainService.createCancelLog(payment);
    }
}
