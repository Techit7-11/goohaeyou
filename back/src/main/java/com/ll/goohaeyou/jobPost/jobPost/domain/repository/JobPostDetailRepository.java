package com.ll.goohaeyou.jobPost.jobPost.domain.repository;

import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobPostDetailRepository extends JpaRepository<JobPostDetail, Long> {
    Optional<JobPostDetail> findByJobPostAndAuthor(JobPost post, String author);

    List<JobPostDetail> findByInterestsMemberId(Long memberId);
}
