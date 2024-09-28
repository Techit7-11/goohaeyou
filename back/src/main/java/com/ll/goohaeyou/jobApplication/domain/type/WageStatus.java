package com.ll.goohaeyou.jobApplication.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WageStatus {
    UNDEFINED("지원서 제출 또는 미승인"),   // 초기 상태

    // 개인 지급
    APPLICATION_APPROVED("지원서 승인"),
    WORK_INCOMPLETE_NO_PAYMENT("작업 미완료로 인한 취소"),
    WAGE_PAID("대금 지급 완료"),

    // 서비스 결제
    PAYMENT_PENDING("대금 결제 전"),
    PAYMENT_COMPLETED("대금 결제 완료"),
    PAYMENT_CANCELLED("대금 취소 완료"),
    SETTLEMENT_REQUESTED("대금 정산 신청"),
    SETTLEMENT_COMPLETED("대금 정산 완료");

    private final String description;
}
