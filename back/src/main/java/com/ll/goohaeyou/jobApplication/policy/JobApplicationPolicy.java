package com.ll.goohaeyou.jobApplication.policy;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.jobApplication.domain.JobApplication;
import com.ll.goohaeyou.jobApplication.exception.JobApplicationException;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import com.ll.goohaeyou.member.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class JobApplicationPolicy {

    public void validateCanWrite(JobPostDetail postDetail, Member member) {
        if (postDetail.getJobPost().isClosed()) {   // 공고가 마감되었는지 확인
            throw new JobApplicationException.ClosedPostExceptionJob();
        }

        if (postDetail.getAuthor().equals(member.getUsername())) {    // 본인의 공고에 지원 불가
            throw new JobApplicationException.NotEligibleForOwnJobExceptionJob();
        }

        // 중복 지원 검증
        for (JobApplication jobApplication : postDetail.getJobApplications()) {
            if (jobApplication.getMember().equals(member)) {
                throw new JobApplicationException.DuplicateSubmissionExceptionJob();
            }
        }
    }

    public void validateCanModify(String username, JobApplication jobApplication) {
        if (!username.equals(jobApplication.getMember().getUsername())) {
            throw new AuthException.NotAuthorizedException();
        }
    }

    // 지원서 삭제 권한 검증
    public void validateCanDelete(String username, JobApplication jobApplication) {
        if (jobApplication.getApprove()) {    // 승인된 지원서는 삭제 불가
            throw new AuthException.NotAuthorizedException();
        }

        if (!username.equals(jobApplication.getMember().getUsername())) {   // 작성자가 맞는지 확인
            throw new AuthException.NotAuthorizedException();
        }
    }
}
