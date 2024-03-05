package com.ll.gooHaeYu.domain.payment.payment.entity;

import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.payment.payment.dto.PaymentResDto;
import com.ll.gooHaeYu.domain.payment.payment.entity.type.PayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Builder
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String paymentKey;

    private Long totalAmount;

    private String payType;

    private String status;   // 상태 업데이트 구현 필요

    private String approvedAt;

    private String orderId;

    private String orderName;

    private boolean paid;

    private boolean canceled;

    private String cancelReason;

    private String failReason;

    private Long applicationId;

    public void updatePayer(Member member) {
        this.member = member;
    }

    public void updatePayType(PayType payType) {
        this.payType = payType.getDescription();
    }

    public void updatePaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }

    public void markAsPaid() {
        this.paid = true;
    }

    public void markAsUnpaid() {
        this.paid = false;
    }

    public void recordFailReason(String failReason) {
        this.failReason = failReason;
    }

    public void recordApprovedAt(String localDateTime) {
        this.approvedAt = localDateTime;
    }

    public PaymentResDto toPaymentRespDto() {
        PayType payTypeEnum = PayType.findPayTypeByDescription(payType);

        return PaymentResDto.builder()
                .payType(payTypeEnum)
                .amount(totalAmount)
                .orderId(orderId)
                .orderName(orderName)
                .payer(member.getUsername())
                .canceled(canceled)
                .failReason(failReason)
                .build();
    }
}
