package com.ll.goohaeyou.domain.jobPost.comment.service;

import com.ll.goohaeyou.domain.jobPost.comment.dto.CommentDto;
import com.ll.goohaeyou.domain.jobPost.comment.dto.CommentForm;
import com.ll.goohaeyou.domain.jobPost.comment.entity.Comment;
import com.ll.goohaeyou.domain.jobPost.comment.entity.repository.CommentRepository;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.goohaeyou.domain.jobPost.jobPost.service.JobPostService;
import com.ll.goohaeyou.domain.member.member.entity.Member;
import com.ll.goohaeyou.domain.member.member.entity.repository.MemberRepository;
import com.ll.goohaeyou.domain.member.member.entity.type.Role;
import com.ll.goohaeyou.domain.member.member.service.MemberService;
import com.ll.goohaeyou.global.event.notification.CommentCreatedEvent;
import com.ll.goohaeyou.global.exception.GoohaeyouException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ll.goohaeyou.global.exception.ErrorCode.*;

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

        if (!canEditeComment(username, comment, postDetail)) throw new GoohaeyouException(NOT_ABLE);

        comment.update(form.getContent());
    }

    @Transactional
    public void deleteComment(String username, Long postId, Long commentId) {
        JobPostDetail postDetail = jobPostService.findByJobPostAndNameAndValidate(postId);
        Comment comment = findByIdAndValidate(commentId);
        Member member = findUserByUserNameValidate(username);

        if (!isAdminOrNot(comment, member)) {
            throw new GoohaeyouException(NOT_ABLE);
        }
        commentRepository.deleteById(commentId);

        postDetail.getJobPost().decreaseCommentsCount();
        postDetail.getComments().remove(comment);
    }

    public List<CommentDto> findByPostId(Long postId) {
        jobPostService.findByIdAndValidate(postId);
        // 공고 유효성 체크를 위해 추가

        return CommentDto.convertToDtoList(commentRepository.findAllByJobPostDetailId(postId));
    }

    public Comment findByIdAndValidate(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new GoohaeyouException(COMMENT_NOT_EXIST));
    }

    private boolean canEditeComment(String username, Comment comment, JobPostDetail post) {
        if (!post.getComments().contains(comment)) {
            throw new GoohaeyouException(COMMENT_NOT_EXIST);
        }

        return username.equals(comment.getMember().getUsername());
    }

    private Member findUserByUserNameValidate(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new GoohaeyouException(MEMBER_NOT_FOUND));
    }

    private boolean isAdminOrNot(Comment comment, Member member) {
        return member.getRole() == Role.ADMIN || comment.getMember().equals(member);
    }

    public List<CommentDto> findByUsername(String username) {

        Member member = memberService.getMember(username);

        return CommentDto.convertToDtoList(commentRepository.findByMemberId(member.getId()));
    }
}
