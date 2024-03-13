package com.ll.gooHaeYu.global.event.notification;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CalculateNotificationEvent extends ApplicationEvent{

    private final Application application;
    public CalculateNotificationEvent(Object source, Application application) {
        super(source);
        this.application = application;
    }
}
