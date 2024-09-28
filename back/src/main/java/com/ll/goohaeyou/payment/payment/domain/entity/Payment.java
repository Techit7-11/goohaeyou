package com.ll.goohaeyou.payment.payment.domain.entity;

import com.ll.goohaeyou.member.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String paymentKey;
    private int totalAmount;
    private String payMethod;
    private String orderId;
    private String orderName;
    private boolean paid;
    private boolean canceled;
    private String cancelReason;
    private Long jobApplicationId;

    private Payment(
            String paymentKey,
            int totalAmount,
            String payMethod,
            String orderId,
            String orderName,
            boolean paid,
            Long jobApplicationId,
            Member member
    ) {
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
        this.payMethod = payMethod;
        this.orderId = orderId;
        this.orderName = orderName;
        this.paid = paid;
        this.jobApplicationId = jobApplicationId;
        this.member = member;
    }

    public static Payment create(String paymentKey, int totalAmount, String payMethod, String orderId, String orderName,
                                 Long jobApplicationId, Member payer) {
        return new Payment(
                paymentKey,
                totalAmount,
                payMethod,
                orderId,
                orderName,
                true,
                jobApplicationId,
                payer
        );
    }

    public void markAsUnpaid() {
        this.paid = false;
    }

    public void cancelPayment(String cancelReason) {
        this.canceled = true;
        this.cancelReason = cancelReason;
    }
}

