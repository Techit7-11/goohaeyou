package com.ll.goohaeyou.domain.payment.payment.dto.success;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SuccessCardDto {
    private String company; // 회사명 (null 찍힘)
    private String number; // 카드번호
    private String installmentPlanMonths; // 할부 개월
    private String isInterestFree;  // 할부 이자
    private String approveNo; // 승인번호
    private String useCardPoint; // 카드 포인트 사용 여부
    private String cardType; // 카드 타입
    private String acquireStatus; // 승인 상태
}
