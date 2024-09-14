package com.ll.goohaeyou.jobPost.employ.domain.policy;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.jobPost.employ.exception.EmployException;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import org.springframework.stereotype.Component;

@Component
public class EmployPolicy {

    public void validateApproval(String username, JobPostDetail postDetail, JobPost jobPost) {
        checkPermissions(username, postDetail.getAuthor());
        validateJobPostIsClosed(jobPost);
    }

    // 사용자 권한 검증
    public void checkPermissions(String username, String author) {
        if (!username.equals(author)) {
            throw new AuthException.NotAuthorizedException();
        }
    }

    // 공고가 마감되었는지 검증
    private void validateJobPostIsClosed(JobPost jobPost) {
        if (!jobPost.isClosed()) {
            throw new EmployException.NotPossibleToApproveItYetExceptionJob();
        }
    }
}
