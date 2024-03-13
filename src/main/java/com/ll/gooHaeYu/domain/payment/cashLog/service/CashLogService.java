package com.ll.gooHaeYu.domain.payment.cashLog.service;

import com.ll.gooHaeYu.domain.application.application.dto.ApplicationDto;
import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.payment.cashLog.dto.CashLogDto;
import com.ll.gooHaeYu.domain.payment.cashLog.entity.CashLog;
import com.ll.gooHaeYu.domain.payment.cashLog.entity.type.EventType;
import com.ll.gooHaeYu.domain.payment.cashLog.repository.CashLogRepository;
import com.ll.gooHaeYu.domain.payment.payment.dto.success.PaymentSuccessDto;
import com.ll.gooHaeYu.domain.payment.payment.entity.Payment;
import com.ll.gooHaeYu.domain.payment.payment.entity.type.PayStatus;
import com.ll.gooHaeYu.domain.payment.payment.entity.type.PayTypeFee;
import com.ll.gooHaeYu.global.event.cashLog.CashLogEvent;
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

    @Transactional
    public void addCashLog(CashLog cashLog) {
        cashLogRepository.save(cashLog);
    }

    public CashLogDto findByApplicationId(Long id) {
        CashLog cashLog = findByApplicationIdAndValidate(id);

        return CashLogDto.fromEntity(cashLog);
    }

    public CashLog findByApplicationIdAndValidate(Long id) {
        return cashLogRepository.findByApplicationId(id)
                .orElseThrow(() -> new CustomException(POST_NOT_EXIST));
    }

    public void createCashLog(Application application) {
        long earn = application.getEarn();
        System.out.println("-----------------------------");
        CashLog newCashLog =  CashLog.builder()
                .member(application.getMember())
                .description("지원서_1_대금_결제")
                .eventType(EventType.대금_이전)
                .totalAmount(earn)
                .vat(getVat(earn))
                .paymentFee(getPaymentFee(PayStatus.EASY_PAY,earn))
                .netAmount(getNetAmount(PayStatus.EASY_PAY,earn))
                .build();
     cashLogRepository.save(newCashLog);
    }
}
