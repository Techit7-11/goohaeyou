package com.ll.goohaeyou.jobApplication.domain;

import com.ll.goohaeyou.global.event.notification.ChangeOfPostEvent;
import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.ModifyJobPostRequest;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.ll.goohaeyou.notification.domain.type.CauseTypeCode.POST_MODIFICATION;
import static com.ll.goohaeyou.notification.domain.type.ResultTypeCode.DELETE;
import static com.ll.goohaeyou.notification.domain.type.ResultTypeCode.NOTICE;

@DomainService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobApplicationDomainService {
    private final ApplicationEventPublisher publisher;

    public void updateApplicationsOnPostModification(JobPostDetail postDetail, ModifyJobPostRequest request) {
        List<JobApplication> applicationsToRemove = new ArrayList<>();
        for (JobApplication jobApplication : postDetail.getJobApplications()) {
            if (request.minAge() > LocalDateTime.now().plusYears(1).getYear() - jobApplication.getMember().getBirth().getYear()) {
                applicationsToRemove.add(jobApplication);
                publisher.publishEvent(new ChangeOfPostEvent(this, postDetail.getJobPost(), jobApplication, POST_MODIFICATION, DELETE));
            } else {
                publisher.publishEvent(new ChangeOfPostEvent(this, postDetail.getJobPost(), jobApplication, POST_MODIFICATION, NOTICE));
            }
        }

        postDetail.getJobApplications().removeAll(applicationsToRemove);
    }
}
