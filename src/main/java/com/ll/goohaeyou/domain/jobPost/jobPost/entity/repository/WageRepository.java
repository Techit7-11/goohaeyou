package com.ll.goohaeyou.domain.jobPost.jobPost.entity.repository;

import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.Wage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WageRepository extends JpaRepository<Wage,Long> {
    Wage findByJobPostDetail(JobPostDetail jobPostDetail);
}
