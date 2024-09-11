package com.ll.goohaeyou.jobPost.employ.application;

import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobApplication.domain.repository.JobApplicationRepository;
import com.ll.goohaeyou.jobApplication.domain.type.WageStatus;
import com.ll.goohaeyou.jobPost.employ.domain.policy.WorkCompletionPolicy;
import com.ll.goohaeyou.jobPost.employ.exception.EmployException;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkCompletionService {
    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostRepository jobPostRepository;
    private final WorkCompletionPolicy workCompletionPolicy;

    @Transactional
    public void completeJobManually(String username, Long applicationId) {
        JobApplication jobApplication = getApplicationWithAuthorizationCheck(username, applicationId);

        updateApplicationByComplete(jobApplication);
    }

    private JobApplication getApplicationWithAuthorizationCheck(String username, Long applicationId) {
        JobApplication jobApplication = jobApplicationRepository.findById(applicationId)
                .orElseThrow(EntityNotFoundException.JobApplicationNotExistsException::new);
        JobPost jobPost = jobPostRepository.findById(jobApplication.getJobPostDetail().getJobPost().getId())
                        .orElseThrow(EntityNotFoundException.PostNotExistsException::new);

        workCompletionPolicy.checkAuthorization(username, jobPost);

        return jobApplication;
    }

    private void updateApplicationByComplete(JobApplication jobApplication) {
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

    // 개인 지급 알바 미완료 처리
    @Transactional
    public void cancelByIndividualPayment(String username, Long applicationId) {
        JobApplication jobApplication = getApplicationWithAuthorizationCheck(username, applicationId);

        workCompletionPolicy.validateWageStatusForCancel(jobApplication);

        updateApplicationByCancel(jobApplication);
    }

    private void updateApplicationByCancel(JobApplication jobApplication) {
        jobApplication.changeToNotCompleted();
        jobApplication.updateWageStatus(WageStatus.WORK_INCOMPLETE_NO_PAYMENT);
    }
}
