package com.ll.gooHaeYu.domain.jobPost.comment.service;

import com.ll.gooHaeYu.domain.jobPost.comment.dto.CommentDto;
import com.ll.gooHaeYu.domain.jobPost.comment.dto.CommentForm;
import com.ll.gooHaeYu.domain.jobPost.comment.entity.Comment;
import com.ll.gooHaeYu.domain.jobPost.comment.repository.CommentRepository;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.repository.JobPostRepository;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.entity.type.Role;
import com.ll.gooHaeYu.domain.member.member.repository.MemberRepository;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import com.ll.gooHaeYu.global.exception.CustomException;
import com.ll.gooHaeYu.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final JobPostService jobPostService;
    private final MemberService memberService;
    private final CommentRepository commentRepository;


    @Transactional
    public Long writeComment(Long postId, String username, CommentForm.Register form) {
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
    public void modifyComment(String username, Long postId, Long commentId, CommentForm.Register form) {
        JobPost post = jobPostService.findByIdAndValidate(postId);
        Comment comment = findByIdAndValidate(commentId);

        if (!canEditeComment(username, comment, post)) throw new CustomException(ErrorCode.NOT_EDITABLE);

        comment.update(form.getContent());
    }

    @Transactional
    public void deleteComment(String username, Long postId, Long commentId) {
        JobPost post = jobPostService.findByIdAndValidate(postId);
        Comment comment = findByIdAndValidate(commentId);
        Member member = memberService.findUserByUserNameValidate(username);

        if (!isAdminOrCommentWriter(comment, member)) {
            throw new CustomException(ErrorCode.NOT_EDITABLE);
        }
        commentRepository.deleteById(commentId);

        post.decreaseCommentsCount();
        post.getComments().remove(comment);
    }


    public List<CommentDto> findByPostId(Long postId) {
        jobPostService.findByIdAndValidate(postId);
        // 공고 유효성 체크를 위해 추가

        return CommentDto.toDtoList(commentRepository.findAllByJobPostId(postId));
    }

    public Comment findByIdAndValidate(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_EXIST));
    }

    private boolean canEditeComment(String username, Comment comment, JobPost post) {
        if (!post.getComments().contains(comment)) {
            throw new CustomException(ErrorCode.COMMENT_NOT_EXIST);
        }

        return username.equals(comment.getMember().getUsername());
    }

    private boolean isAdminOrCommentWriter(Comment comment, Member member) {
        return member.getRole() == Role.ADMIN || comment.getMember().equals(member);
    }


}
