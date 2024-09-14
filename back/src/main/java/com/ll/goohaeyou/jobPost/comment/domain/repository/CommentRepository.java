package com.ll.goohaeyou.jobPost.comment.domain.repository;

import com.ll.goohaeyou.jobPost.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByJobPostDetailId(Long jobPostId);

    List<Comment> findByMemberId(Long id);
}
