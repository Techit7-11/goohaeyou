package com.ll.goohaeyou.jobPost.comment.domain;

import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.jobPost.comment.domain.repository.CommentRepository;
import com.ll.goohaeyou.jobPost.comment.exception.CommentException;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@DomainService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentDomainService {
    private final CommentRepository commentRepository;

    @Transactional
    public Comment create(JobPostDetail postDetail, Member member, String content) {
        Comment newComment = Comment.create(postDetail, member, content);

        return commentRepository.save(newComment);
    }

    @Transactional
    public void delete(JobPostDetail postDetail, Comment comment) {
        postDetail.getJobPost().decreaseCommentsCount();
        postDetail.getComments().remove(comment);
        commentRepository.delete(comment);
    }

    public Comment getById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(CommentException.CommentNotExistsException::new);
    }

    public List<Comment> getByPostDetailId(Long postId) {
        return commentRepository.findAllByJobPostDetailId(postId);
    }

    public List<Comment> getByMemberId(Long memberId) {
        return commentRepository.findByMemberId(memberId);
    }
}
