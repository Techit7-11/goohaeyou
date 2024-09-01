package com.ll.goohaeyou.payment.cashLog.application;

import com.ll.goohaeyou.application.domain.Application;
import com.ll.goohaeyou.payment.cashLog.domain.CashLog;
import com.ll.goohaeyou.payment.cashLog.domain.repository.CashLogRepository;
import com.ll.goohaeyou.payment.cashLog.domain.type.EventType;
import com.ll.goohaeyou.payment.payment.application.dto.success.PaymentSuccessDto;
import com.ll.goohaeyou.payment.payment.domain.Payment;
import com.ll.goohaeyou.payment.payment.domain.type.PayStatus;
import com.ll.goohaeyou.payment.payment.application.PaymentCalculationService;
import com.ll.goohaeyou.jobPost.jobPost.exception.JobPostException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CashLogService {
    private final PaymentCalculationService paymentCalculationService;
    private final CashLogRepository cashLogRepository;

    public CashLog findByApplicationIdAndValidate(Long id) {
        return cashLogRepository.findByApplicationId(id)
                .orElseThrow(JobPostException.PostNotExistsException::new);
    }

    private void addCashLog(CashLog cashLog) {
        cashLogRepository.save(cashLog);
    }

    @Transactional
    public void createCashLogOnSettled(Application application) {
        long earn = application.getEarn();

        CashLog newCashLog =  CashLog.builder()
                .member(application.getMember())
                .description("지원서_" + application.getId() + "_대금_결제")
                .eventType(EventType.정산_급여)
                .totalAmount(earn)
                .vat(paymentCalculationService.getVat(earn))
                .paymentFee(paymentCalculationService.getPaymentFee(PayStatus.EASY_PAY,earn))
                .netAmount(paymentCalculationService.getNetAmount(PayStatus.EASY_PAY,earn))
                .applicationId(application.getId())
                .build();

        addCashLog(newCashLog);
    }

    @Transactional
    public void createCashLogOnPaid(PaymentSuccessDto successDto, Payment payment) {
        PayStatus payStatus = PayStatus.findByMethod(successDto.getMethod());

        CashLog newCashLog =  CashLog.builder()
                .member(payment.getMember())
                .description(successDto.getOrderName())
                .eventType(EventType.결제_토스페이먼츠)
                .totalAmount(successDto.getTotalAmount())
                .vat(paymentCalculationService.getVat(payment.getTotalAmount()))
                .paymentFee(paymentCalculationService.getPaymentFee(payStatus, payment.getTotalAmount()))
                .netAmount(paymentCalculationService.getNetAmount(payStatus, payment.getTotalAmount()))
                .applicationId(payment.getApplicationId())
                .build();

        addCashLog(newCashLog);
    }

    @Transactional
    public void createCashLogOnCancel(Payment payment) {
        CashLog newCashLog = CashLog.builder()
                .member(payment.getMember())
                .description("지원서_" + payment.getApplicationId() + "_급여_결제취소")
                .eventType(EventType.취소_토스페이먼츠)
                .totalAmount(payment.getTotalAmount())
                .vat(0)
                .paymentFee(0)
                .netAmount(payment.getTotalAmount())
                .applicationId(payment.getApplicationId())
                .build();

        addCashLog(newCashLog);
    }
}
