package com.ll.goohaeyou.payment.cashLog.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Getter
@Builder
@Table(name = "cash_log")
public class CashLog extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private long totalAmount = 0;    // 거래 금액

    private long vat = 0;    // 부가세

    private long paymentFee = 0;    // 결제수수료

    private long netAmount = 0;   // 순금액

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Member member;

    private Long applicationId;
}
