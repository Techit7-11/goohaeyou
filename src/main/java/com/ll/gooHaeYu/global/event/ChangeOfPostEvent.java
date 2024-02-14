package com.ll.gooHaeYu.global.event;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.notification.entity.type.CauseTypeCode;
import com.ll.gooHaeYu.domain.notification.entity.type.ResultTypeCode;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ChangeOfPostEvent extends ApplicationEvent {
    private final JobPost jobPost;
    private final Application application;
    private final CauseTypeCode causeTypeCode;
    private final ResultTypeCode resultTypeCode;

    public ChangeOfPostEvent(Object object, JobPost jobPost, Application application,CauseTypeCode causeTypeCode, ResultTypeCode resultTypeCode) {
        super(object);
        this.jobPost = jobPost;
        this.application = application;
        this.causeTypeCode = causeTypeCode;
        this.resultTypeCode = resultTypeCode;
    }
}
