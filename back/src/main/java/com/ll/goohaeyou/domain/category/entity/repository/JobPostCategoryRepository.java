package com.ll.goohaeyou.domain.category.entity.repository;

import com.ll.goohaeyou.domain.category.entity.JobPostCategory;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostCategoryRepository extends JpaRepository<JobPostCategory, Long> {
    void deleteAllByJobPost(JobPost jobPost);
}
