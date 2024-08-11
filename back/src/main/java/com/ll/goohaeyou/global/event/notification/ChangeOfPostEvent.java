package com.ll.goohaeyou.global.event.notification;

import com.ll.goohaeyou.application.domain.Application;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPost;
import com.ll.goohaeyou.notification.domain.type.CauseTypeCode;
import com.ll.goohaeyou.notification.domain.type.ResultTypeCode;
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
