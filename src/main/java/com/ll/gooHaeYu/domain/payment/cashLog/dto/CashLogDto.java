package com.ll.gooHaeYu.domain.payment.cashLog.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.gooHaeYu.domain.application.application.dto.ApplicationDto;
import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.payment.cashLog.entity.CashLog;
import com.ll.gooHaeYu.domain.payment.cashLog.entity.type.EventType;
import jakarta.persistence.*;
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

    public static CashLogDto fromEntity(CashLog cashLog) {
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
