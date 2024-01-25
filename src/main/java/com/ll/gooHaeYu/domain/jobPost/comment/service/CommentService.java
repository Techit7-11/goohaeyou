package com.ll.gooHaeYu.domain.jobPost.comment.service;

import com.ll.gooHaeYu.domain.jobPost.comment.dto.CommentForm;
import com.ll.gooHaeYu.domain.jobPost.comment.entity.Comment;
import com.ll.gooHaeYu.domain.jobPost.comment.repository.CommentRepository;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import com.ll.gooHaeYu.global.exception.CustomException;
import com.ll.gooHaeYu.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final JobPostService jobPostService;
    private final MemberService memberService;
    private final CommentRepository commentRepository;

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

    @Transactional
    public void modifyComment(String username, Long postId, Long commentId, CommentForm.Register form){
        JobPost post = jobPostService.findByIdAndValidate(postId);
        Comment comment = findByIdAndValidate(commentId);

        if (!canEditeComment(username, comment, post)) throw new CustomException(ErrorCode.NOT_EDITABLE);


        comment.update(form.getContent());
    }

    public Comment findByIdAndValidate(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_EXIST));
    }

    private boolean canEditeComment (String username, Comment comment, JobPost post) {
        if (!post.getComments().contains(comment)) {
            throw new CustomException(ErrorCode.COMMENT_NOT_EXIST);
        }

        return username.equals(comment.getMember().getUsername());
    }
}
