package com.ll.gooHaeYu.domain.jobPost.employ.service;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.application.application.entity.type.DepositStatus;
import com.ll.gooHaeYu.domain.application.application.service.ApplicationService;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ll.gooHaeYu.global.exception.ErrorCode.BAD_REQUEST;
import static com.ll.gooHaeYu.global.exception.ErrorCode.NOT_ABLE;

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

        application.updateDepositStatus(DepositStatus.SETTLEMENT_REQUESTED);
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
}
