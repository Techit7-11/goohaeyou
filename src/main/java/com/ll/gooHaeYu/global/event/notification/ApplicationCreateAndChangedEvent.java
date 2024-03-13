package com.ll.gooHaeYu.global.event.notification;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.gooHaeYu.domain.notification.entity.type.CauseTypeCode;
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
