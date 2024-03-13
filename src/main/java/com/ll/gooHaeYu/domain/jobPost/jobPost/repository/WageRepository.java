package com.ll.gooHaeYu.domain.jobPost.jobPost.repository;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.Wage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WageRepository extends JpaRepository<Wage,Long> {
    Wage findByJobPostDetail(JobPostDetail jobPostDetail);
}
