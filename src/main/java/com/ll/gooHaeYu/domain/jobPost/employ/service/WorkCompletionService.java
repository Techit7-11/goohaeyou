package com.ll.gooHaeYu.domain.jobPost.employ.service;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.application.application.entity.type.WageStatus;
import com.ll.gooHaeYu.domain.application.application.service.ApplicationService;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ll.gooHaeYu.domain.application.application.entity.type.WageStatus.*;
import static com.ll.gooHaeYu.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class WorkCompletionService {
    private final ApplicationService applicationService;
    private final JobPostService jobPostService;

    // 수동 알바완료 처리
    @Transactional
    public void completeJobManually(String username, Long applicationId) {
        Application application = getApplicationWithAuthorizationCheck(username, applicationId);

        updateApplicationByComplete(application);
    }

    private Application getApplicationWithAuthorizationCheck(String username, Long applicationId) {
        Application application = applicationService.findByIdAndValidate(applicationId);
        JobPost jobPost = jobPostService.findByIdAndValidate(application.getJobPostDetail().getJobPost().getId());

        checkAuthorization(username, jobPost);

        return application;
    }

    private void checkAuthorization(String username, JobPost jobPost) {
        if (!jobPost.getJobPostDetail().getAuthor().equals(username)) {
            throw new CustomException(NOT_ABLE);
        }
    }

    private void updateApplicationByComplete(Application application) {
        application.changeToCompleted();

        switch (application.getWageStatus()) {
            case PAYMENT_COMPLETED -> application.updateWageStatus(WageStatus.SETTLEMENT_REQUESTED);
            case APPLICATION_APPROVED -> {
                application.updateWageStatus(WageStatus.WAGE_PAID);
                application.setReceive(true);
            }
            default -> throw new CustomException(COMPLETION_NOT_POSSIBLE);
        }
    }

    // 개인 지급 알바 미완료 처리
    @Transactional
    public void cancelByIndividualPayment(String username, Long applicationId) {
        Application application = getApplicationWithAuthorizationCheck(username, applicationId);

        if (!application.getWageStatus().equals(APPLICATION_APPROVED)) {
            throw new CustomException(BAD_REQUEST);
        }

        updateApplicationByCancel(application);
    }

    private void updateApplicationByCancel(Application application) {
        application.changeToNotCompleted();
        application.updateWageStatus(WageStatus.WORK_INCOMPLETE_NO_PAYMENT);
    }
}
