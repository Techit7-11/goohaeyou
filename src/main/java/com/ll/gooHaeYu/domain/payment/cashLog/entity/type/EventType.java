package com.ll.gooHaeYu.domain.payment.cashLog.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {

    결제_토스페이먼츠,
    취소_토스페이먼츠,
    정산_급여,
    출금_통장입금;
}
