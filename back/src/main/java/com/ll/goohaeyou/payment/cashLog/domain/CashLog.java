package com.ll.goohaeyou.payment.cashLog.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.goohaeyou.jobApplication.domain.JobApplication;
import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.payment.cashLog.domain.type.EventType;
import com.ll.goohaeyou.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "cash_log")
public class CashLog extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private long totalAmount;    // 거래 금액

    private long vat;    // 부가세

    private long paymentFee;    // 결제수수료

    private long netAmount;   // 순금액

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Member member;

    private Long jobApplicationId;

    private CashLog(
            String description,
            EventType eventType,
            long totalAmount,
            long vat,
            long paymentFee,
            long netAmount,
            Member member,
            Long jobApplicationId
    ) {
        this.description = description;
        this.eventType = eventType;
        this.totalAmount = totalAmount;
        this.vat = vat;
        this.paymentFee = paymentFee;
        this.netAmount = netAmount;
        this.member = member;
        this.jobApplicationId = jobApplicationId;
    }

    public static CashLog createOnSettlement(
            long totalAmount,
            long vat,
            long paymentFee,
            long netAmount,
            Member member,
            Long jobApplicationId
    ) {
        return new CashLog(
                "지원서_" + jobApplicationId + "_대금_결제",
                EventType.정산_급여,
                totalAmount,
                vat,
                paymentFee,
                netAmount,
                member,
                jobApplicationId
        );
    }

    public static CashLog createOnPaymentSuccess(
            long totalAmount,
            long vat,
            long paymentFee,
            long netAmount,
            Member member,
            Long jobApplicationId
    ) {
        return new CashLog(
                "지원서_" + jobApplicationId + "_대금_결제",
                EventType.결제_토스페이먼츠,
                totalAmount,
                vat,
                paymentFee,
                netAmount,
                member,
                jobApplicationId
        );
    }

    public static CashLog createOnCancel(
            long totalAmount,
            Member member,
            Long jobApplicationId
    ) {
        return new CashLog(
                "지원서_" + jobApplicationId + "_급여_결제취소",
                EventType.취소_토스페이먼츠,
                totalAmount,
                0,
                0,
                totalAmount,
                member,
                jobApplicationId
        );
    }
}
