package com.ll.goohaeyou.jobPost.jobPost.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum WagePaymentMethod {
    UNDEFINED("미설정"),
    INDIVIDUAL_PAYMENT("개인 지급"),
    SERVICE_PAYMENT("서비스 결제");

    private final String description;
}
