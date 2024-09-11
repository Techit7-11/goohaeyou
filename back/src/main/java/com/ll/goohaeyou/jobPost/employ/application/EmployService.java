package com.ll.goohaeyou.jobPost.employ.application;

import com.ll.goohaeyou.global.event.notification.ChangeOfPostEvent;
import com.ll.goohaeyou.global.event.notification.CreateChatRoomEvent;
import com.ll.goohaeyou.global.event.notification.PostEmployedEvent;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.jobApplication.application.dto.JobPostApplicationResponse;
import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobApplication.domain.type.WageStatus;
import com.ll.goohaeyou.jobPost.employ.domain.policy.EmployPolicy;
import com.ll.goohaeyou.jobPost.employ.exception.EmployException;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostDetailRepository;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostRepository;
import com.ll.goohaeyou.jobPost.jobPost.domain.type.WagePaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.ll.goohaeyou.notification.domain.type.CauseTypeCode.APPLICATION_APPROVED;
import static com.ll.goohaeyou.notification.domain.type.CauseTypeCode.APPLICATION_UNAPPROVED;
import static com.ll.goohaeyou.notification.domain.type.ResultTypeCode.DELETE;
import static com.ll.goohaeyou.notification.domain.type.ResultTypeCode.NOTICE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployService {
    private final JobPostRepository jobPostRepository;
    private final JobPostDetailRepository jobPostDetailRepository;
    private final ApplicationEventPublisher publisher;
    private final EmployPolicy employPolicy;

    public List<JobPostApplicationResponse> getList(String username, Long postId) {
        JobPostDetail postDetail = jobPostDetailRepository.findById(postId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);

        employPolicy.checkPermissions(username, postDetail.getAuthor());

        return JobPostApplicationResponse.convertToList(postDetail.getJobApplications());
    }

    @Transactional
    public void approve(String username, Long postId, List<Long> applicationIds) {
        JobPost jobPost = jobPostRepository.findById(postId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);
        JobPostDetail postDetail = jobPost.getJobPostDetail();
        Long postWriterId = jobPost.getMember().getId();

        WageStatus updateWageStatus = determineWageStatus(postDetail.getWage().getWagePaymentMethod());

        employPolicy.validateApproval(username, postDetail, jobPost);

        List<JobApplication> jobApplicationList = new ArrayList<>();

        for (JobApplication jobApplication : postDetail.getJobApplications()) {
            if(applicationIds.contains(jobApplication.getId())) {
                Long receiverId = jobApplication.getMember().getId();
                jobApplication.approve();

                jobApplication.updateWageStatus(updateWageStatus);
                jobApplication.updateJobEndDate(jobPost.getJobStartDate()
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

    private WageStatus determineWageStatus(WagePaymentMethod wagePaymentMethod) {
        return switch (wagePaymentMethod) {
            case SERVICE_PAYMENT -> WageStatus.PAYMENT_PENDING;
            case INDIVIDUAL_PAYMENT -> WageStatus.APPLICATION_APPROVED;
            default -> throw new EmployException.InvalidWagePaymentMethodException();
        };
    }

    private void increaseApplicantTransactionCount(JobApplication jobApplication) {
        if (!Objects.isNull(jobApplication)) {
            jobApplication.getMember().increaseTransactionCount();
        }
    }

    private void increaseAuthorTransactionCount(JobPost jobPost) {
        if (!Objects.isNull(jobPost)) {
            jobPost.getMember().increaseTransactionCount();
        }
    }
}
