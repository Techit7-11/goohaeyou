package com.ll.gooHaeYu.domain.payment.cashLog.service;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.payment.cashLog.dto.CashLogDto;
import com.ll.gooHaeYu.domain.payment.cashLog.entity.CashLog;
import com.ll.gooHaeYu.domain.payment.cashLog.entity.type.EventType;
import com.ll.gooHaeYu.domain.payment.cashLog.repository.CashLogRepository;
import com.ll.gooHaeYu.domain.payment.payment.dto.success.PaymentSuccessDto;
import com.ll.gooHaeYu.domain.payment.payment.entity.Payment;
import com.ll.gooHaeYu.domain.payment.payment.entity.type.PayStatus;
import com.ll.gooHaeYu.domain.payment.payment.service.PaymentCalculationService;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ll.gooHaeYu.global.exception.ErrorCode.POST_NOT_EXIST;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CashLogService {
    private final PaymentCalculationService paymentCalculationService;
    private final CashLogRepository cashLogRepository;

    public CashLogDto findByApplicationId(Long id) {
        CashLog cashLog = findByApplicationIdAndValidate(id);

        return CashLogDto.fromEntity(cashLog);
    }

    public CashLog findByApplicationIdAndValidate(Long id) {
        return cashLogRepository.findByApplicationId(id)
                .orElseThrow(() -> new CustomException(POST_NOT_EXIST));
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
