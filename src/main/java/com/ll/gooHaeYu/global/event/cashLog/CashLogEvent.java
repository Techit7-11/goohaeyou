package com.ll.gooHaeYu.global.event.cashLog;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.payment.cashLog.entity.CashLog;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CashLogEvent extends ApplicationEvent {
    private final Application application;
    public CashLogEvent(Object source, Application application) {
        super(source);
        this.application = application;
    }
}
