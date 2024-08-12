package com.ll.goohaeyou.jobPost.comment.application;

import com.ll.goohaeyou.jobPost.comment.dto.CommentDto;
import com.ll.goohaeyou.jobPost.comment.dto.CommentForm;
import com.ll.goohaeyou.jobPost.comment.domain.Comment;
import com.ll.goohaeyou.jobPost.comment.domain.repository.CommentRepository;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.application.JobPostService;
import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
import com.ll.goohaeyou.member.member.domain.type.Role;
import com.ll.goohaeyou.member.member.application.MemberService;
import com.ll.goohaeyou.global.event.notification.CommentCreatedEvent;
import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.jobPost.comment.exception.CommentException;
import com.ll.goohaeyou.member.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public CommentForm.Register writeComment(Long postId, String username, CommentForm.Register form) {
        JobPostDetail postDetail = jobPostService.findByJobPostAndNameAndValidate(postId);

        Comment comment = Comment.builder()
                .jobPostDetail(postDetail)
                .member(memberService.getMember(username))
                .content(form.getContent())
                .build();

        postDetail.getComments().add(comment);
        postDetail.getJobPost().increaseCommentsCount();

        if(!postDetail.getAuthor().equals(username)) {
            publisher.publishEvent(new CommentCreatedEvent(this,postDetail,comment));
        }

        return form;
    }

    @Transactional
    public void modifyComment(String username, Long postId, Long commentId, CommentForm.Register form) {
        JobPostDetail postDetail = jobPostService.findByJobPostAndNameAndValidate(postId);
        Comment comment = findByIdAndValidate(commentId);

        if (!canEditComment(username, comment, postDetail)) {
            throw new AuthException.NotAuthorizedException();
        }

        comment.update(form.getContent());
    }

    @Transactional
    public void deleteComment(String username, Long postId, Long commentId) {
        JobPostDetail postDetail = jobPostService.findByJobPostAndNameAndValidate(postId);
        Comment comment = findByIdAndValidate(commentId);
        Member member = findUserByUserNameValidate(username);

        if (!isAdminOrNot(comment, member)) {
            throw new AuthException.NotAuthorizedException();
        }

        commentRepository.deleteById(commentId);

        postDetail.getJobPost().decreaseCommentsCount();
        postDetail.getComments().remove(comment);
    }

    public List<CommentDto> findByPostId(Long postId) {
        jobPostService.findByIdAndValidate(postId);

        return CommentDto.convertToDtoList(commentRepository.findAllByJobPostDetailId(postId));
    }

    public Comment findByIdAndValidate(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(CommentException.CommentNotExistsException::new);
    }

    private boolean canEditComment(String username, Comment comment, JobPostDetail post) {
        if (!post.getComments().contains(comment)) {
            throw new CommentException.CommentNotExistsException();
        }

        return username.equals(comment.getMember().getUsername());
    }

    private Member findUserByUserNameValidate(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(MemberException.MemberNotFoundException::new);
    }

    private boolean isAdminOrNot(Comment comment, Member member) {
        return member.getRole() == Role.ADMIN || comment.getMember().equals(member);
    }

    public List<CommentDto> findByUsername(String username) {

        Member member = memberService.getMember(username);

        return CommentDto.convertToDtoList(commentRepository.findByMemberId(member.getId()));
    }
}
