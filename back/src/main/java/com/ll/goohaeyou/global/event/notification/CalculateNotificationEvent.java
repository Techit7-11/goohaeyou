package com.ll.goohaeyou.global.event.notification;

import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CalculateNotificationEvent extends ApplicationEvent{
    private final JobApplication jobApplication;

    public CalculateNotificationEvent(Object source, JobApplication jobApplication) {
        super(source);
        this.jobApplication = jobApplication;
    }
}
