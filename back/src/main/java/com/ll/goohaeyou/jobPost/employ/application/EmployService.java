package com.ll.goohaeyou.jobPost.employ.application;

import com.ll.goohaeyou.application.dto.ApplicationDto;
import com.ll.goohaeyou.application.domain.Application;
import com.ll.goohaeyou.application.domain.type.WageStatus;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.type.WagePaymentMethod;
import com.ll.goohaeyou.jobPost.jobPost.application.JobPostService;
import com.ll.goohaeyou.global.event.notification.ChangeOfPostEvent;
import com.ll.goohaeyou.global.event.notification.CreateChatRoomEvent;
import com.ll.goohaeyou.global.event.notification.PostEmployedEvent;
import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.jobPost.employ.exception.EmployException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.ll.goohaeyou.notification.domain.type.CauseTypeCode.APPLICATION_APPROVED;
import static com.ll.goohaeyou.notification.domain.type.CauseTypeCode.APPLICATION_UNAPPROVED;
import static com.ll.goohaeyou.notification.domain.type.ResultTypeCode.DELETE;
import static com.ll.goohaeyou.notification.domain.type.ResultTypeCode.NOTICE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployService {
    private final JobPostService jobPostService;
    private final ApplicationEventPublisher publisher;

    public List<ApplicationDto> getList(String username, Long postId) {
        JobPostDetail postDetail = jobPostService.findByJobPostAndNameAndValidate(postId);
        checkPermissions(username,postDetail.getAuthor());

        return ApplicationDto.convertToDtoList(postDetail.getApplications());
    }

    @Transactional
    public void approve(String username, Long postId, List<Long> applicationIds) {
        JobPost jobPost = jobPostService.findByIdAndValidate(postId);
        JobPostDetail postDetail = jobPost.getJobPostDetail();
        Long postWriterId = jobPost.getMember().getId();

        WageStatus updateWageStatus = determineWageStatus(postDetail.getWage().getWagePaymentMethod());

        if (!jobPost.isClosed()) {
            throw new EmployException.NotPossibleToApproveItYetException();
        }

        checkPermissions(username,postDetail.getAuthor());

        List<Application> applicationList = new ArrayList<>();

        for (Application application : postDetail.getApplications()) {
            if(applicationIds.contains(application.getId())) {
                Long receiverId = application.getMember().getId();
                application.approve();

                application.updateWageStatus(updateWageStatus);
                application.setJobEndDate(jobPost.getJobStartDate()
                        .plusDays(postDetail.getWage().getWorkDays() - 1));

                increaseApplicantTransactionCount(application);
                increaseAuthorTransactionCount(jobPost);

                publisher.publishEvent(new ChangeOfPostEvent(this, jobPost, application,APPLICATION_APPROVED, NOTICE));
                publisher.publishEvent(new CreateChatRoomEvent(this,postWriterId,receiverId,jobPost.getTitle()));

            } else {
                application.reject();
                applicationList.add(application);
                publisher.publishEvent(new ChangeOfPostEvent(this, jobPost, application, APPLICATION_UNAPPROVED, DELETE));
            }
        }

        for (Application application : applicationList) {
            postDetail.getApplications().remove(application);
        }

        publisher.publishEvent(new PostEmployedEvent(this, jobPost));
    }

    public void checkPermissions (String username, String author){
        if (!username.equals(author)) {
            throw new AuthException.NotAuthorizedException();
        }
    }

    private WageStatus determineWageStatus(WagePaymentMethod wagePaymentMethod) {
        return switch (wagePaymentMethod) {
            case SERVICE_PAYMENT -> WageStatus.PAYMENT_PENDING;
            case INDIVIDUAL_PAYMENT -> WageStatus.APPLICATION_APPROVED;
            default -> throw new EmployException.InvalidWagePaymentMethodException();
        };
    }

    private void increaseApplicantTransactionCount(Application application) {
        if (isValidApplication(application)) {
            application.getMember().increaseTransactionCount();
        }
    }

    private void increaseAuthorTransactionCount(JobPost jobPost) {
        if (isValidJobPost(jobPost)) {
            jobPost.getMember().increaseTransactionCount();
        }
    }

    private boolean isValidApplication(Application application) {
        return application != null && application.getMember() != null;
    }

    private boolean isValidJobPost(JobPost jobPost) {
        return jobPost != null && jobPost.getMember() != null;
    }
}
