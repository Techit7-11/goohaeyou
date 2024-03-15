package com.ll.gooHaeYu.domain.payment.cashLog.service;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.payment.cashLog.dto.CashLogDto;
import com.ll.gooHaeYu.domain.payment.cashLog.entity.CashLog;
import com.ll.gooHaeYu.domain.payment.cashLog.entity.type.EventType;
import com.ll.gooHaeYu.domain.payment.cashLog.repository.CashLogRepository;
import com.ll.gooHaeYu.domain.payment.payment.dto.success.PaymentSuccessDto;
import com.ll.gooHaeYu.domain.payment.payment.entity.Payment;
import com.ll.gooHaeYu.domain.payment.payment.entity.type.PayStatus;
import com.ll.gooHaeYu.domain.payment.payment.entity.type.PayTypeFee;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static com.ll.gooHaeYu.global.exception.ErrorCode.NO_ENUM_CONSTANT_FOUND;
import static com.ll.gooHaeYu.global.exception.ErrorCode.POST_NOT_EXIST;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CashLogService {
    private final CashLogRepository cashLogRepository;

    // 토스페이먼츠 결제 부과세 10% 반환
    public long getVat(long totalAmount) {
        return (int) (totalAmount * 0.1);
    }

    // 결제 수수료 반환
    public long getPaymentFee(PayStatus payStatus, long totalAmount) {
        PayTypeFee payTypeFee = matchPayTypeFee(payStatus);

        double feePercentage = payTypeFee.getFeePercentage();
        int transactionFee = payTypeFee.getTransactionFee();

        return (int) ((totalAmount * feePercentage / 100.0) + transactionFee);
    }

    private PayTypeFee matchPayTypeFee(PayStatus payStatus) {
        return Arrays.stream(PayTypeFee.values())
                .filter(payTypeFee -> payTypeFee.getTypeName().equals(payStatus.getDescription()))
                .findFirst()
                .orElseThrow(() -> new CustomException(NO_ENUM_CONSTANT_FOUND));   // 매칭되는 PayTypeFee가 없는 경우
    }

    // 부가세와 결제 수수료의 합 반환
    public long getTotalTaxAndFees(PayStatus payStatus, long totalAmount) {
        return getVat(totalAmount) + getPaymentFee(payStatus, totalAmount);
    }

    // 순금액 반환
    public long getNetAmount (PayStatus payStatus, long totalAmount) {
        return totalAmount - getTotalTaxAndFees(payStatus, totalAmount);
    }

    public CashLogDto findByApplicationId(Long id) {
        CashLog cashLog = findByApplicationIdAndValidate(id);

        return CashLogDto.fromEntity(cashLog);
    }

    public CashLog findByApplicationIdAndValidate(Long id) {
        return cashLogRepository.findByApplicationId(id)
                .orElseThrow(() -> new CustomException(POST_NOT_EXIST));
    }

    @Transactional
    public void addCashLog(CashLog cashLog) {
        cashLogRepository.save(cashLog);
    }

    public void createCashLog(Application application) {
        long earn = application.getEarn();
        CashLog newCashLog =  CashLog.builder()
                .member(application.getMember())
                .description("지원서_" + application.getId() + "_대금_결제")
                .eventType(EventType.정산_급여)
                .totalAmount(earn)
                .vat(getVat(earn))
                .paymentFee(getPaymentFee(PayStatus.EASY_PAY,earn))
                .netAmount(getNetAmount(PayStatus.EASY_PAY,earn))
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
                .vat(getVat(payment.getTotalAmount()))
                .paymentFee(getPaymentFee(payStatus, payment.getTotalAmount()))
                .netAmount(getNetAmount(payStatus, payment.getTotalAmount()))
                .applicationId(payment.getApplicationId())
                .build();

        addCashLog(newCashLog);
    }

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
