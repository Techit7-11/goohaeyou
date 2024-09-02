package com.ll.goohaeyou.jobPost.employ.application;

import com.ll.goohaeyou.jobApplication.domain.JobApplication;
import com.ll.goohaeyou.jobApplication.domain.type.WageStatus;
import com.ll.goohaeyou.jobApplication.application.JobApplicationService;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.application.JobPostService;
import com.ll.goohaeyou.global.exception.GoohaeyouException;
import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.jobPost.employ.exception.EmployException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ll.goohaeyou.jobApplication.domain.type.WageStatus.APPLICATION_APPROVED;
import static com.ll.goohaeyou.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class WorkCompletionService {
    private final JobApplicationService jobApplicationService;
    private final JobPostService jobPostService;

    @Transactional
    public void completeJobManually(String username, Long applicationId) {
        JobApplication jobApplication = getApplicationWithAuthorizationCheck(username, applicationId);

        updateApplicationByComplete(jobApplication);
    }

    private JobApplication getApplicationWithAuthorizationCheck(String username, Long applicationId) {
        JobApplication jobApplication = jobApplicationService.findByIdAndValidate(applicationId);
        JobPost jobPost = jobPostService.findByIdAndValidate(jobApplication.getJobPostDetail().getJobPost().getId());

        checkAuthorization(username, jobPost);

        return jobApplication;
    }

    private void checkAuthorization(String username, JobPost jobPost) {
        if (!jobPost.getJobPostDetail().getAuthor().equals(username)) {
            throw new AuthException.NotAuthorizedException();
        }
    }

    private void updateApplicationByComplete(JobApplication jobApplication) {
        jobApplication.changeToCompleted();

        switch (jobApplication.getWageStatus()) {
            case PAYMENT_COMPLETED -> jobApplication.updateWageStatus(WageStatus.SETTLEMENT_REQUESTED);
            case APPLICATION_APPROVED -> {
                jobApplication.updateWageStatus(WageStatus.WAGE_PAID);
                jobApplication.setReceive(true);
            }
            default -> throw new EmployException.CompletionNotPossibleException();
        }
    }

    // 개인 지급 알바 미완료 처리
    @Transactional
    public void cancelByIndividualPayment(String username, Long applicationId) {
        JobApplication jobApplication = getApplicationWithAuthorizationCheck(username, applicationId);

        if (!jobApplication.getWageStatus().equals(APPLICATION_APPROVED)) {
            throw new GoohaeyouException(BAD_REQUEST);
        }

        updateApplicationByCancel(jobApplication);
    }

    private void updateApplicationByCancel(JobApplication jobApplication) {
        jobApplication.changeToNotCompleted();
        jobApplication.updateWageStatus(WageStatus.WORK_INCOMPLETE_NO_PAYMENT);
    }
}
