package com.ll.gooHaeYu.domain.jobPost.jobPost.repository;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostImageRepository extends JpaRepository<JobPostImage, Long> {
    List<JobPostImage> findByJobPostDetailId(Long jobPostDetailId);
}
