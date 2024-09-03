package com.ll.goohaeyou.payment.payment.domain;

import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.payment.payment.application.dto.request.PaymentReqDto;
import com.ll.goohaeyou.payment.payment.application.dto.request.PaymentResDto;
import com.ll.goohaeyou.payment.payment.domain.type.PayStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
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
    private String payStatus;
    private String orderId;
    private String orderName;
    private boolean paid;
    private boolean canceled;
    private String cancelReason;
    private Long jobApplicationId;

    @Version
    private Long version;

    private Payment(
            Long totalAmount,
            String payStatus,
            String orderId,
            String orderName,
            Long jobApplicationId
    ) {
        this.totalAmount = totalAmount;
        this.payStatus = payStatus;
        this.orderId = orderId;
        this.orderName = orderName;
        this.jobApplicationId = jobApplicationId;
    }

    public static Payment from(PaymentReqDto dto) {
        return new Payment(
                dto.getAmount(),
                dto.getPayStatus().getDescription(),
                UUID.randomUUID().toString(),
                dto.getOrderName(),
                dto.getJobApplicationId()
        );
    }

    public void updatePayer(Member member) {
        this.member = member;
    }

    public void updatePayStatus(PayStatus payStatus) {
        this.payStatus = payStatus.getDescription();
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

    public void cancelPayment(String cancelReason) {
        this.canceled = true;
        this.cancelReason = cancelReason;
    }

    public PaymentResDto toPaymentRespDto() {
        PayStatus payStatusEnum = PayStatus.findPayTypeByDescription(payStatus);

        return PaymentResDto.builder()
                .payStatus(payStatusEnum)
                .amount(totalAmount)
                .orderId(orderId)
                .orderName(orderName)
                .payer(member.getUsername())
                .canceled(canceled)
                .build();
    }
}

