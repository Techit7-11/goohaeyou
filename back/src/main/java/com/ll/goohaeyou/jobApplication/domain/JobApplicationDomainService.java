package com.ll.goohaeyou.jobApplication.domain;

import com.ll.goohaeyou.global.event.notification.ChangeOfPostEvent;
import com.ll.goohaeyou.global.event.notification.CreateChatRoomEvent;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.jobApplication.application.dto.WriteJobApplicationRequest;
import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobApplication.domain.repository.JobApplicationRepository;
import com.ll.goohaeyou.jobApplication.domain.type.WageStatus;
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

    public List<JobApplication> approveOrRejectApplications(JobPostDetail postDetail, JobPost jobPost, List<Long> applicationIds, WageStatus updateWageStatus) {
        List<JobApplication> jobApplicationList = new ArrayList<>();
        Long postWriterId = jobPost.getMember().getId();

        for (JobApplication jobApplication : postDetail.getJobApplications()) {
            if(applicationIds.contains(jobApplication.getId())) {
                jobApplication.approve();
                jobApplication.updateWageStatus(updateWageStatus);
                jobApplication.updateJobEndDate(jobPost.getJobStartDate()
                        .plusDays(postDetail.getWage().getWorkDays() - 1));

                increaseApplicantTransactionCount(jobApplication);
                increaseAuthorTransactionCount(jobPost);

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

    public void removeUnapprovedApplications(JobPostDetail postDetail, List<JobApplication> jobApplicationList) {
        for (JobApplication jobApplication : jobApplicationList) {
            postDetail.getJobApplications().remove(jobApplication);
        }
    }

    private void increaseApplicantTransactionCount(JobApplication jobApplication) {
        if (jobApplication != null) {
            jobApplication.getMember().increaseTransactionCount();
        }
    }

    private void increaseAuthorTransactionCount(JobPost jobPost) {
        if (jobPost != null) {
            jobPost.getMember().increaseTransactionCount();
        }
    }
}
