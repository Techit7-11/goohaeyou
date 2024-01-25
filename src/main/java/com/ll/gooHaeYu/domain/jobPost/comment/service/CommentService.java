package com.ll.gooHaeYu.domain.jobPost.comment.service;

import com.ll.gooHaeYu.domain.jobPost.comment.dto.CommentForm;
import com.ll.gooHaeYu.domain.jobPost.comment.entity.Comment;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final JobPostService jobPostService;
    private final MemberService memberService;

    @Transactional
    public Long writeComment (Long postId, String username, CommentForm.Register form) {
        JobPost post = jobPostService.findByIdAndValidate(postId);
        Comment comment = Comment.builder()
                .jobPost(jobPostService.findByIdAndValidate(postId))
                .member(memberService.getMember(username))
                .content(form.getContent())
                .build();
        post.getComments().add(comment);
        post.increaseCommentsCount();

        return postId;
    }
}
