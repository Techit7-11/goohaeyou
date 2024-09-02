package com.ll.goohaeyou.global.event.notification;

import com.ll.goohaeyou.jobApplication.domain.JobApplication;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPost;
import com.ll.goohaeyou.notification.domain.type.CauseTypeCode;
import com.ll.goohaeyou.notification.domain.type.ResultTypeCode;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ChangeOfPostEvent extends ApplicationEvent {
    private final JobPost jobPost;
    private final JobApplication jobApplication;
    private final CauseTypeCode causeTypeCode;
    private final ResultTypeCode resultTypeCode;

    public ChangeOfPostEvent(Object object, JobPost jobPost, JobApplication jobApplication, CauseTypeCode causeTypeCode, ResultTypeCode resultTypeCode) {
        super(object);
        this.jobPost = jobPost;
        this.jobApplication = jobApplication;
        this.causeTypeCode = causeTypeCode;
        this.resultTypeCode = resultTypeCode;
    }
}
