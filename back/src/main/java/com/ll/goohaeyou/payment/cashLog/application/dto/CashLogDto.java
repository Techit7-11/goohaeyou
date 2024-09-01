package com.ll.goohaeyou.payment.cashLog.application.dto;

import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.payment.cashLog.domain.CashLog;
import com.ll.goohaeyou.payment.cashLog.domain.type.EventType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CashLogDto {
    @NotNull
    private Long id;
    @NotNull
    private String description;
    @NotNull
    private EventType eventType;
    @NotNull
    private long totalAmount;
    @NotNull
    private long vat;
    @NotNull
    private long paymentFee;
    @NotNull
    private long netAmount;
    @NotNull
    private Member member;
    @NotNull
    private Long applicationId;

    public static CashLogDto from(CashLog cashLog) {
        return CashLogDto.builder()
                .id(cashLog.getId())
                .description(cashLog.getDescription())
                .eventType(cashLog.getEventType())
                .totalAmount(cashLog.getTotalAmount())
                .vat(cashLog.getVat())
                .paymentFee(cashLog.getPaymentFee())
                .netAmount(cashLog.getNetAmount())
                .member(cashLog.getMember())
                .applicationId(cashLog.getApplicationId())
                .build();
    }
}
