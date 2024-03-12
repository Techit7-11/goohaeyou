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

import static com.ll.gooHaeYu.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkCompletionService {
    private final ApplicationService applicationService;
    private final JobPostService jobPostService;

    @Transactional
    public void completeJobManually(String username, Long applicationId) {
        Application application = applicationService.findByIdAndValidate(applicationId);
        JobPost jobPost = jobPostService.findByIdAndValidate(application.getJobPostDetail().getJobPost().getId());

        checkPermissions(username, jobPost);

        updateApplicationStatus(application);
        application.changeToCompleted();
    }

    private void checkPermissions(String username, JobPost jobPost) {
        if (!jobPost.getJobPostDetail().getAuthor().equals(username)) {
            throw new CustomException(NOT_ABLE);
        }
        if (!jobPost.isEmployed() || !jobPost.isClosed()) {
            throw new CustomException(BAD_REQUEST);
        }
    }

    private void updateApplicationStatus(Application application) {
        switch (application.getWageStatus()) {
            case PAYMENT_COMPLETED:
                application.updateWageStatus(WageStatus.SETTLEMENT_REQUESTED);
                break;
            case APPLICATION_APPROVED:
                application.updateWageStatus(WageStatus.WAGE_PAID);
                application.setReceive(true);
                break;
            default:
                throw new CustomException(COMPLETION_NOT_POSSIBLE);
        }
    }

}
