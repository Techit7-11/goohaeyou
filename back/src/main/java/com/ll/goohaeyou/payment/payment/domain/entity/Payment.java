package com.ll.goohaeyou.payment.payment.domain.entity;

import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.payment.payment.domain.type.PayStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public static Payment create(Long totalAmount, String payStatus, String orderId, String orderName,
                                 Long jobApplicationId) {
        return new Payment(
                totalAmount,
                payStatus,
                orderId,
                orderName,
                jobApplicationId
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
}

