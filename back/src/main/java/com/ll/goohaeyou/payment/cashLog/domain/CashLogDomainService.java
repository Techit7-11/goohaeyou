package com.ll.goohaeyou.payment.cashLog.domain;

import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.payment.cashLog.domain.entity.CashLog;
import com.ll.goohaeyou.payment.cashLog.domain.repository.CashLogRepository;
import com.ll.goohaeyou.payment.payment.application.dto.success.PaymentSuccessResponse;
import com.ll.goohaeyou.payment.payment.domain.entity.Payment;
import com.ll.goohaeyou.payment.payment.domain.type.PayStatus;
import com.ll.goohaeyou.payment.payment.domain.type.PayTypeFee;
import com.ll.goohaeyou.payment.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@DomainService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CashLogDomainService {
    private final CashLogRepository cashLogRepository;

    @Transactional
    public void createSettlementLog(JobApplication jobApplication) {
        long earn = jobApplication.getEarn();

        CashLog newCashLog = CashLog.createOnSettlement(
                earn,
                getVat(earn),
                getPaymentFee(PayStatus.EASY_PAY, earn),
                getNetAmount(PayStatus.EASY_PAY, earn),
                jobApplication.getMember(),
                jobApplication.getId()
        );

        cashLogRepository.save(newCashLog);
    }

    @Transactional
    public void createPaymentLog(Payment payment, PaymentSuccessResponse successDto) {
        PayStatus payStatus = PayStatus.findByMethod(successDto.method());

        CashLog newCashLog = CashLog.createOnPaymentSuccess(
                successDto.totalAmount(),
                getVat(payment.getTotalAmount()),
                getPaymentFee(payStatus, payment.getTotalAmount()),
                getNetAmount(payStatus, payment.getTotalAmount()),
                payment.getMember(),
                payment.getJobApplicationId()
        );

        cashLogRepository.save(newCashLog);
    }

    @Transactional
    public void createCancelLog(Payment payment) {
        CashLog newCashLog = CashLog.createOnCancel(
                payment.getTotalAmount(),
                payment.getMember(),
                payment.getJobApplicationId()
        );

        cashLogRepository.save(newCashLog);
    }

    public CashLog getByApplicationId(Long id) {
        return cashLogRepository.findByJobApplicationId(id)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);
    }

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
                .orElseThrow(PaymentException.NoEnumConstantFoundException::new);
    }

    // 부가세와 결제 수수료의 합 반환
    public long getTotalTaxAndFees(PayStatus payStatus, long totalAmount) {
        return getVat(totalAmount) + getPaymentFee(payStatus, totalAmount);
    }

    // 순금액 반환
    public long getNetAmount(PayStatus payStatus, long totalAmount) {
        return totalAmount - getTotalTaxAndFees(payStatus, totalAmount);
    }
}
