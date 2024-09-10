package com.ll.goohaeyou.jobPost.jobPost.domain.policy;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostRepository;
import com.ll.goohaeyou.member.member.domain.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobPostPolicy {
    private final JobPostRepository jobPostRepository;

    public void validateCanModify(String username, Long postId) {
        JobPost jobPost = jobPostRepository.findById(postId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);
        if (!username.equals(jobPost.getMember().getUsername())) {
            throw new AuthException.NotAuthorizedException();
        }
    }

    public void validateCanDelete(String username, Long postId) {
        JobPost jobPost = jobPostRepository.findById(postId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);

        if (Role.ADMIN.equals(jobPost.getMember().getRole()) || !username.equals(jobPost.getMember().getUsername())) {
            throw new AuthException.NotAuthorizedException();
        }
    }
}
