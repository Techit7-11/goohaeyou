package com.ll.goohaeyou.jobPost.jobPost.domain.repository;

import com.ll.goohaeyou.jobPost.jobPost.domain.entity.Essential;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EssentialRepository extends JpaRepository<Essential,Long> {
    Essential findByJobPostDetail(JobPostDetail jobPostDetail);
}
