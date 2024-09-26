package com.ll.goohaeyou.jobPost.employ.application;

import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobApplication.domain.service.JobApplicationDomainService;
import com.ll.goohaeyou.jobPost.employ.domain.policy.WorkCompletionPolicy;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.service.JobPostDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkCompletionService {
    private final WorkCompletionPolicy workCompletionPolicy;
    private final JobApplicationDomainService jobApplicationDomainService;
    private final JobPostDomainService jobPostDomainService;

    @Transactional
    public void completeJobManually(String username, Long applicationId) {
        JobApplication jobApplication = getApplicationWithAuthorizationCheck(username, applicationId);

        jobApplicationDomainService.updateByWorkComplete(jobApplication);
    }

    private JobApplication getApplicationWithAuthorizationCheck(String username, Long applicationId) {
        JobApplication jobApplication = jobApplicationDomainService.getById(applicationId);
        JobPost jobPost = jobPostDomainService.findById(jobApplication.getJobPostDetail().getJobPost().getId());

        workCompletionPolicy.checkAuthorization(username, jobPost);

        return jobApplication;
    }

    // 개인 지급 알바 미완료 처리
    @Transactional
    public void cancelByIndividualPayment(String username, Long applicationId) {
        JobApplication jobApplication = getApplicationWithAuthorizationCheck(username, applicationId);

        workCompletionPolicy.validateWageStatusForCancel(jobApplication);

        jobApplicationDomainService.updateByWorkCancel(jobApplication);
    }
}
