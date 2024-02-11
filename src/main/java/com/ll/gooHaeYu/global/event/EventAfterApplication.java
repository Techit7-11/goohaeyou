package com.ll.gooHaeYu.global.event;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterApplication extends ApplicationEvent {
    private final Application application;

    public EventAfterApplication(Object object, Application application) {
        super(object);
        this.application = application;
    }
}
