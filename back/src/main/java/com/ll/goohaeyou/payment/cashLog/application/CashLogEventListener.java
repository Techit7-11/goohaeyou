package com.ll.goohaeyou.payment.cashLog.application;

import com.ll.goohaeyou.global.event.cashLog.CashLogEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CashLogEventListener {
    private final CashLogService cashLogService;

    @EventListener
    public void creatCashLogEvent(CashLogEvent event) {
        cashLogService.createCashLogOnSettled(event.getApplication());
    }
}
