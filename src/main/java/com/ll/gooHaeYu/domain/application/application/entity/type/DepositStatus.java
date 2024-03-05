package com.ll.gooHaeYu.domain.application.application.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DepositStatus {
    UNDEFINED("지원서 제출 또는 미승인"),
    APPLICATION_APPROVED("지원서 승인"),
    DEPOSIT_PAID("예치금 결제 완료"),
    DEPOSIT_CANCELLED("예치금 취소 완료"),
    SETTLEMENT_REQUESTED("정산 신청"),
    SETTLEMENT_COMPLETED("정산 완료");

    private final String description;
}
