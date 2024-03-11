package com.ll.gooHaeYu.domain.payment.cashLog.service;

import com.ll.gooHaeYu.domain.payment.cashLog.entity.CashLog;
import com.ll.gooHaeYu.domain.payment.cashLog.repository.CashLogRepository;
import com.ll.gooHaeYu.domain.payment.payment.entity.type.PayStatus;
import com.ll.gooHaeYu.domain.payment.payment.entity.type.PayTypeFee;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static com.ll.gooHaeYu.global.exception.ErrorCode.NO_ENUM_CONSTANT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
}
