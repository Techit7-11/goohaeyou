package com.ll.goohaeyou.jobPost.employ.application;

import com.ll.goohaeyou.jobApplication.application.dto.JobApplicationDto;
import com.ll.goohaeyou.jobApplication.domain.JobApplication;
import com.ll.goohaeyou.jobApplication.domain.type.WageStatus;
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

    public List<JobApplicationDto> getList(String username, Long postId) {
        JobPostDetail postDetail = jobPostService.findByJobPostAndNameAndValidate(postId);
        checkPermissions(username,postDetail.getAuthor());

        return JobApplicationDto.convertToDtoList(postDetail.getJobApplications());
    }

    @Transactional
    public void approve(String username, Long postId, List<Long> applicationIds) {
        JobPost jobPost = jobPostService.findByIdAndValidate(postId);
        JobPostDetail postDetail = jobPost.getJobPostDetail();
        Long postWriterId = jobPost.getMember().getId();

        WageStatus updateWageStatus = determineWageStatus(postDetail.getWage().getWagePaymentMethod());

        if (!jobPost.isClosed()) {
            throw new EmployException.NotPossibleToApproveItYetExceptionJob();
        }

        checkPermissions(username,postDetail.getAuthor());

        List<JobApplication> jobApplicationList = new ArrayList<>();

        for (JobApplication jobApplication : postDetail.getJobApplications()) {
            if(applicationIds.contains(jobApplication.getId())) {
                Long receiverId = jobApplication.getMember().getId();
                jobApplication.approve();

                jobApplication.updateWageStatus(updateWageStatus);
                jobApplication.setJobEndDate(jobPost.getJobStartDate()
                        .plusDays(postDetail.getWage().getWorkDays() - 1));

                increaseApplicantTransactionCount(jobApplication);
                increaseAuthorTransactionCount(jobPost);

                publisher.publishEvent(new ChangeOfPostEvent(this, jobPost, jobApplication,APPLICATION_APPROVED, NOTICE));
                publisher.publishEvent(new CreateChatRoomEvent(this,postWriterId,receiverId,jobPost.getTitle()));

            } else {
                jobApplication.reject();
                jobApplicationList.add(jobApplication);
                publisher.publishEvent(new ChangeOfPostEvent(this, jobPost, jobApplication, APPLICATION_UNAPPROVED, DELETE));
            }
        }

        for (JobApplication jobApplication : jobApplicationList) {
            postDetail.getJobApplications().remove(jobApplication);
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

    private void increaseApplicantTransactionCount(JobApplication jobApplication) {
        if (isValidApplication(jobApplication)) {
            jobApplication.getMember().increaseTransactionCount();
        }
    }

    private void increaseAuthorTransactionCount(JobPost jobPost) {
        if (isValidJobPost(jobPost)) {
            jobPost.getMember().increaseTransactionCount();
        }
    }

    private boolean isValidApplication(JobApplication jobApplication) {
        return jobApplication != null && jobApplication.getMember() != null;
    }

    private boolean isValidJobPost(JobPost jobPost) {
        return jobPost != null && jobPost.getMember() != null;
    }
}
