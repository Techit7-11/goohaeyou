package com.ll.goohaeyou.global.event.cashLog;

import com.ll.goohaeyou.jobApplication.domain.JobApplication;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CashLogEvent extends ApplicationEvent {
    private final JobApplication jobApplication;

    public CashLogEvent(Object source, JobApplication jobApplication) {
        super(source);
        this.jobApplication = jobApplication;
    }
}
