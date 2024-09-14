package com.ll.goohaeyou.jobPost.comment.application;

import com.ll.goohaeyou.global.event.notification.CommentCreatedEvent;
import com.ll.goohaeyou.jobPost.comment.application.dto.*;
import com.ll.goohaeyou.jobPost.comment.domain.Comment;
import com.ll.goohaeyou.jobPost.comment.domain.service.CommentDomainService;
import com.ll.goohaeyou.jobPost.comment.domain.policy.CommentPolicy;
import com.ll.goohaeyou.jobPost.jobPost.domain.service.JobPostDetailDomainService;
import com.ll.goohaeyou.jobPost.jobPost.domain.service.JobPostDomainService;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.member.member.domain.service.MemberDomainService;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final ApplicationEventPublisher publisher;
    private final CommentPolicy commentPolicy;
    private final MemberDomainService memberDomainService;
    private final JobPostDomainService jobPostDomainService;
    private final JobPostDetailDomainService jobPostDetailDomainService;
    private final CommentDomainService commentDomainService;

    @Transactional
    public CreateCommentResponse writeComment(Long postId, String username, CreateCommentRequest request) {
        JobPostDetail postDetail = jobPostDetailDomainService.getById(postId);
        Member member = memberDomainService.getByUsername(username);

        Comment newComment = commentDomainService.create(postDetail, member, request.content());

        jobPostDomainService.addCommentToPost(postDetail, newComment);

        if(!postDetail.getAuthor().equals(username)) {
            publisher.publishEvent(new CommentCreatedEvent(this,postDetail, newComment));
        }

        return new CreateCommentResponse(newComment.getContent());
    }

    @Transactional
    public void modifyComment(String username, Long postId, Long commentId, ModifyCommentRequest request) {
        JobPostDetail postDetail = jobPostDetailDomainService.getById(postId);
        Comment comment = commentDomainService.getById(commentId);

        commentPolicy.validateCanEdit(username, comment, postDetail);

        comment.update(request.content());
    }

    @Transactional
    public void deleteComment(String username, Long postId, Long commentId) {
        JobPostDetail postDetail = jobPostDetailDomainService.getById(postId);
        Comment comment = commentDomainService.getById(commentId);
        Member member = memberDomainService.getByUsername(username);

        commentPolicy.validateCanDelete(comment, member);

        commentDomainService.delete(postDetail, comment);
    }

    public List<CommentResponse> findByPostId(Long postId) {
        jobPostDomainService.findById(postId);

        return CommentResponse.convertToList(commentDomainService.getByPostDetailId(postId));
    }

    public List<MyCommentResponse> findByUsername(String username) {
        Member member = memberDomainService.getByUsername(username);

        return MyCommentResponse.convertToList(commentDomainService.getByMemberId(member.getId()));
    }
}
