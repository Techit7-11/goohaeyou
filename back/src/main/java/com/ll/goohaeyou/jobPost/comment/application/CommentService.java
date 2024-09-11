package com.ll.goohaeyou.jobPost.comment.application;

import com.ll.goohaeyou.global.event.notification.CommentCreatedEvent;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.jobPost.comment.application.dto.*;
import com.ll.goohaeyou.jobPost.comment.domain.Comment;
import com.ll.goohaeyou.jobPost.comment.domain.policy.CommentPolicy;
import com.ll.goohaeyou.jobPost.comment.domain.repository.CommentRepository;
import com.ll.goohaeyou.jobPost.comment.exception.CommentException;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostDetailRepository;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostRepository;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
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
    private final JobPostRepository jobPostRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher publisher;
    private final JobPostDetailRepository jobPostDetailRepository;
    private final CommentPolicy commentPolicy;

    @Transactional
    public CreateCommentResponse writeComment(Long postId, String username, CreateCommentRequest request) {
        JobPostDetail postDetail = jobPostDetailRepository.findById(postId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);

        Comment newComment = Comment.create(postDetail, member, request.content());

        postDetail.getComments().add(newComment);
        postDetail.getJobPost().increaseCommentsCount();

        if(!postDetail.getAuthor().equals(username)) {
            publisher.publishEvent(new CommentCreatedEvent(this,postDetail, newComment));
        }

        return new CreateCommentResponse(newComment.getContent());
    }

    @Transactional
    public void modifyComment(String username, Long postId, Long commentId, ModifyCommentRequest request) {
        JobPostDetail postDetail = jobPostDetailRepository.findById(postId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);
        Comment comment = findByIdAndValidate(commentId);

        commentPolicy.validateCanEdit(username, comment, postDetail);

        comment.update(request.content());
    }

    @Transactional
    public void deleteComment(String username, Long postId, Long commentId) {
        JobPostDetail postDetail = jobPostDetailRepository.findById(postId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);
        Comment comment = findByIdAndValidate(commentId);
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(MemberException.MemberNotFoundException::new);

        commentPolicy.validateCanDelete(comment, member);

        commentRepository.deleteById(commentId);

        postDetail.getJobPost().decreaseCommentsCount();
        postDetail.getComments().remove(comment);
    }

    public List<CommentResponse> findByPostId(Long postId) {
        jobPostRepository.findById(postId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);

        return CommentResponse.convertToList(commentRepository.findAllByJobPostDetailId(postId));
    }

    public Comment findByIdAndValidate(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(CommentException.CommentNotExistsException::new);
    }

    public List<MyCommentResponse> findByUsername(String username) {

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);

        return MyCommentResponse.convertToList(commentRepository.findByMemberId(member.getId()));
    }
}
