package com.ll.goohaeyou.jobPost.employ.application;

import com.ll.goohaeyou.global.event.notification.PostEmployedEvent;
import com.ll.goohaeyou.jobApplication.application.dto.JobPostApplicationResponse;
import com.ll.goohaeyou.jobApplication.domain.service.JobApplicationDomainService;
import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobApplication.domain.type.WageStatus;
import com.ll.goohaeyou.jobPost.employ.domain.service.EmployDomainService;
import com.ll.goohaeyou.jobPost.employ.domain.policy.EmployPolicy;
import com.ll.goohaeyou.jobPost.jobPost.domain.service.JobPostDetailDomainService;
import com.ll.goohaeyou.jobPost.jobPost.domain.service.JobPostDomainService;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployService {
    private final ApplicationEventPublisher publisher;
    private final EmployPolicy employPolicy;
    private final JobPostDetailDomainService jobPostDetailDomainService;
    private final JobPostDomainService jobPostDomainService;
    private final EmployDomainService employDomainService;
    private final JobApplicationDomainService jobApplicationDomainService;

    public List<JobPostApplicationResponse> getList(String username, Long postId) {
        JobPostDetail postDetail = jobPostDetailDomainService.getById(postId);

        employPolicy.checkPermissions(username, postDetail.getAuthor());

        return JobPostApplicationResponse.convertToList(postDetail.getJobApplications());
    }

    @Transactional
    public void approve(String username, Long postId, List<Long> applicationIds) {
        JobPost jobPost = jobPostDomainService.findById(postId);
        JobPostDetail postDetail = jobPost.getJobPostDetail();

        WageStatus updateWageStatus = employDomainService.determineWageStatus(postDetail.getWage().getWagePaymentMethod());

        employPolicy.validateApproval(username, postDetail, jobPost);

        List<JobApplication> jobApplicationList = jobApplicationDomainService.approveOrRejectApplications(postDetail, jobPost, applicationIds, updateWageStatus);

        jobApplicationDomainService.removeUnapprovedApplications(postDetail, jobApplicationList);

        publisher.publishEvent(new PostEmployedEvent(this, jobPost));
    }
}
