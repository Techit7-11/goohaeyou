package com.ll.goohaeyou.global.event.notification;

import com.ll.goohaeyou.application.domain.Application;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import com.ll.goohaeyou.notification.domain.type.CauseTypeCode;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ApplicationCreateAndChangedEvent extends ApplicationEvent {
    private final JobPostDetail jobPostDetail;
    private final Application application;
    private final CauseTypeCode causeTypeCode;

    public ApplicationCreateAndChangedEvent(Object object, JobPostDetail jobPostDetail, Application application, CauseTypeCode causeTypeCode) {
        super(object);
        this.jobPostDetail = jobPostDetail;
        this.application = application;
        this.causeTypeCode = causeTypeCode;
    }

    public ApplicationCreateAndChangedEvent(Object object, Application application, CauseTypeCode causeTypeCode) {
        super(object);
        this.jobPostDetail = application.getJobPostDetail();
        this.application = application;
        this.causeTypeCode = causeTypeCode;
    }
}
