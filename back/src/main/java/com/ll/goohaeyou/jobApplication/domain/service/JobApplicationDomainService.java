package com.ll.goohaeyou.jobApplication.domain.service;

import com.ll.goohaeyou.global.event.notification.ChangeOfPostEvent;
import com.ll.goohaeyou.global.event.notification.CreateChatRoomEvent;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.jobApplication.application.dto.WriteJobApplicationRequest;
import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobApplication.domain.repository.JobApplicationRepository;
import com.ll.goohaeyou.jobApplication.domain.type.WageStatus;
import com.ll.goohaeyou.jobPost.employ.exception.EmployException;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.ModifyJobPostRequest;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.ll.goohaeyou.jobApplication.domain.type.WageStatus.PAYMENT_CANCELLED;
import static com.ll.goohaeyou.notification.domain.type.CauseTypeCode.*;
import static com.ll.goohaeyou.notification.domain.type.ResultTypeCode.DELETE;
import static com.ll.goohaeyou.notification.domain.type.ResultTypeCode.NOTICE;

@DomainService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobApplicationDomainService {
    private final ApplicationEventPublisher publisher;
    private final JobApplicationRepository jobApplicationRepository;

    @Transactional
    public JobApplication create(Member member, JobPostDetail postDetail, WriteJobApplicationRequest request) {
        return jobApplicationRepository.save(JobApplication.create(member, postDetail, request.body()));

    }

    @Transactional
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

    @Transactional
    public void deleteById(Long id) {
        jobApplicationRepository.deleteById(id);
    }

    public JobApplication getById(Long id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(EntityNotFoundException.JobApplicationNotExistsException::new);
    }

    public List<JobApplication> getByMemberId(Long memberId) {
        return jobApplicationRepository.findByMemberId(memberId);
    }

    @Transactional
    public List<JobApplication> approveOrRejectApplications(JobPostDetail postDetail, JobPost jobPost, List<Long> applicationIds, WageStatus updateWageStatus) {
        List<JobApplication> jobApplicationList = new ArrayList<>();
        Long postWriterId = jobPost.getMember().getId();

        for (JobApplication jobApplication : postDetail.getJobApplications()) {
            if(applicationIds.contains(jobApplication.getId())) {
                jobApplication.approve();
                jobApplication.updateWageStatus(updateWageStatus);
                jobApplication.updateJobEndDate(jobPost.getJobStartDate()
                        .plusDays(postDetail.getWage().getWorkDays() - 1));

                jobApplication.getMember().increaseTransactionCount();
                jobPost.getMember().increaseTransactionCount();

                publisher.publishEvent(new ChangeOfPostEvent(this, jobPost, jobApplication, APPLICATION_APPROVED, NOTICE));
                publisher.publishEvent(new CreateChatRoomEvent(this, postWriterId, jobApplication.getMember().getId(), jobPost.getTitle()));
            } else {
                jobApplication.reject();
                jobApplicationList.add(jobApplication);
                publisher.publishEvent(new ChangeOfPostEvent(this, jobPost, jobApplication, APPLICATION_UNAPPROVED, DELETE));
            }
        }
        return jobApplicationList;
    }

    @Transactional
    public void removeUnapprovedApplications(JobPostDetail postDetail, List<JobApplication> jobApplicationList) {
        for (JobApplication jobApplication : jobApplicationList) {
            postDetail.getJobApplications().remove(jobApplication);
        }
    }

    @Transactional
    public void updateStatusByPaymentSuccess(JobApplication jobApplication, Long amount) {
        jobApplication.updateWageStatus(WageStatus.PAYMENT_COMPLETED);
        jobApplication.updateEarn(Math.toIntExact(amount));
    }

    @Transactional
    public void updateOnPaymentCancel(JobApplication jobApplication) {
        jobApplication.updateWageStatus(PAYMENT_CANCELLED);
        jobApplication.updateEarn(0);
        jobApplication.changeToNotCompleted();
    }

    @Transactional
    public void updateByWorkComplete(JobApplication jobApplication) {
        jobApplication.changeToCompleted();

        switch (jobApplication.getWageStatus()) {
            case PAYMENT_COMPLETED -> jobApplication.updateWageStatus(WageStatus.SETTLEMENT_REQUESTED);
            case APPLICATION_APPROVED -> {
                jobApplication.updateWageStatus(WageStatus.WAGE_PAID);
                jobApplication.markAsReceived(true);
            }
            default -> throw new EmployException.CompletionNotPossibleException();
        }
    }

    @Transactional
    public void updateByWorkCancel(JobApplication jobApplication) {
        jobApplication.changeToNotCompleted();
        jobApplication.updateWageStatus(WageStatus.WORK_INCOMPLETE_NO_PAYMENT);
    }
}
