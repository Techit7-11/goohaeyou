package com.ll.goohaeyou.jobPost.employ.domain.policy;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.global.exception.GoohaeyouException;
import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobApplication.domain.type.WageStatus;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import org.springframework.stereotype.Component;

import static com.ll.goohaeyou.global.exception.ErrorCode.BAD_REQUEST;

@Component
public class WorkCompletionPolicy {

    public void checkAuthorization(String username, JobPost jobPost) {
        if (!jobPost.getJobPostDetail().getAuthor().equals(username)) {
            throw new AuthException.NotAuthorizedException();
        }
    }

    public void validateWageStatusForCancel(JobApplication jobApplication) {
        if (!jobApplication.getWageStatus().equals(WageStatus.APPLICATION_APPROVED)) {
            throw new GoohaeyouException(BAD_REQUEST);
        }
    }
}
