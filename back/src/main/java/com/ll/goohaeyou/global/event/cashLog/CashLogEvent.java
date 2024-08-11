package com.ll.goohaeyou.global.event.cashLog;

import com.ll.goohaeyou.application.domain.Application;
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
