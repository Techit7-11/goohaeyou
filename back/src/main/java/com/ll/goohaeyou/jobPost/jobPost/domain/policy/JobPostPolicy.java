package com.ll.goohaeyou.jobPost.jobPost.domain.policy;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.member.member.domain.type.Role;
import org.springframework.stereotype.Component;

@Component
public class JobPostPolicy {

    public void validateCanModify(String username, JobPost jobPost) {
        if (!username.equals(jobPost.getMember().getUsername())) {
            throw new AuthException.NotAuthorizedException();
        }
    }

    public void validateCanDelete(String username, JobPost jobPost) {
        if (Role.ADMIN.equals(jobPost.getMember().getRole()) || !username.equals(jobPost.getMember().getUsername())) {
            throw new AuthException.NotAuthorizedException();
        }
    }

    public void validateCanClose(String username, JobPost jobPost) {
        if (!username.equals(jobPost.getMember().getUsername())) {
            throw new AuthException.NotAuthorizedException();
        }
    }
}
