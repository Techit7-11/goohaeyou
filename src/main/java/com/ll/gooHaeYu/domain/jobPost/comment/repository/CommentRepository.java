package com.ll.gooHaeYu.domain.jobPost.comment.repository;

import com.ll.gooHaeYu.domain.jobPost.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByJobPostDetailId(Long jobPostId);

    List<Comment> findByMemberId(Long id);
}
