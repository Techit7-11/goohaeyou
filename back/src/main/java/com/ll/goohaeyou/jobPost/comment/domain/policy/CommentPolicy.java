package com.ll.goohaeyou.jobPost.comment.domain.policy;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.jobPost.comment.domain.Comment;
import com.ll.goohaeyou.jobPost.comment.exception.CommentException;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.member.member.domain.type.Role;
import org.springframework.stereotype.Component;

@Component
public class CommentPolicy {

    public void validateCanEdit(String username, Comment comment, JobPostDetail postDetail) {
        if (!postDetail.getComments().contains(comment)) {
            throw new CommentException.CommentNotExistsException();
        }

        if (!username.equals(comment.getMember().getUsername())) {
            throw new AuthException.NotAuthorizedException();
        }
    }

    public void validateCanDelete(Comment comment, Member member) {
        if (member.getRole() != Role.ADMIN && !comment.getMember().equals(member)) {
            throw new AuthException.NotAuthorizedException();
        }
    }
}
