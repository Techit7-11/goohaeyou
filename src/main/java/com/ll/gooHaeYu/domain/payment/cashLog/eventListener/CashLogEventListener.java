package com.ll.gooHaeYu.domain.payment.cashLog.eventListener;

import com.ll.gooHaeYu.domain.payment.cashLog.service.CashLogService;
import com.ll.gooHaeYu.global.event.cashLog.CashLogEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CashLogEventListener {
    private final CashLogService cashLogService;

    @EventListener
    public void creatCashLogEvent(CashLogEvent event) {
        cashLogService.createCashLog(event.getApplication());
    }
}
