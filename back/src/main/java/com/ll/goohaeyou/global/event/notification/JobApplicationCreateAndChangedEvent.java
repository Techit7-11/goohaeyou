package com.ll.goohaeyou.global.event.notification;

import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.notification.domain.type.CauseTypeCode;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class JobApplicationCreateAndChangedEvent extends ApplicationEvent {
    private final JobPostDetail jobPostDetail;
    private final JobApplication jobApplication;
    private final CauseTypeCode causeTypeCode;

    public JobApplicationCreateAndChangedEvent(Object object, JobPostDetail jobPostDetail, JobApplication jobApplication, CauseTypeCode causeTypeCode) {
        super(object);
        this.jobPostDetail = jobPostDetail;
        this.jobApplication = jobApplication;
        this.causeTypeCode = causeTypeCode;
    }

    public JobApplicationCreateAndChangedEvent(Object object, JobApplication jobApplication, CauseTypeCode causeTypeCode) {
        super(object);
        this.jobPostDetail = jobApplication.getJobPostDetail();
        this.jobApplication = jobApplication;
        this.causeTypeCode = causeTypeCode;
    }
}
