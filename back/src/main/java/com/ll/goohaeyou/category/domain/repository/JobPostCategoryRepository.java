package com.ll.goohaeyou.category.domain.repository;

import com.ll.goohaeyou.category.domain.type.CategoryType;
import com.ll.goohaeyou.category.domain.entity.JobPostCategory;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostCategoryRepository extends JpaRepository<JobPostCategory, Long> {
    @Query("SELECT jpc.jobPost FROM JobPostCategory jpc WHERE jpc.category.id = :categoryId ORDER BY jpc.jobPost.createdAt DESC")
    Page<JobPost> findJobPostsByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    void deleteAllByJobPost(JobPost jobPost);

    JobPostCategory findByJobPostAndCategory_Type(JobPost jobPost, CategoryType categoryType);
}
