package com.ll.goohaeyou.review.domain.policy;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.review.exception.ReviewException;
import org.springframework.stereotype.Component;

@Component
public class ReviewPolicy {

    public void verifyReviewAuthor(Member applicant, String currentUsername) {
        if (!applicant.getUsername().equals(currentUsername)) {
            throw new AuthException.NotAuthorizedException();
        }
    }

    public void verifyReviewNotExists(boolean exists) {
        if (exists) {
            throw new ReviewException.ReviewAlreadyExistsException();
        }
    }
}