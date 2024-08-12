package com.ll.goohaeyou.category.application;

import com.ll.goohaeyou.category.domain.Category;
import com.ll.goohaeyou.category.domain.repository.JobPostCategoryRepository;
import com.ll.goohaeyou.category.domain.type.CategoryType;
import com.ll.goohaeyou.category.domain.JobPostCategory;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class JobPostCategoryService {
    private final JobPostCategoryRepository jobPostCategoryRepository;

    public JobPostCategory findByJobPostAndCategoryType(JobPost jobPost, CategoryType categoryType) {
        return jobPostCategoryRepository.findByJobPostAndCategory_Type(jobPost, categoryType);
    }

    public void createAndSaveJobPostCategory(JobPost jobPost, Category category) {
        JobPostCategory jobPostCategory = JobPostCategory.builder()
                .jobPost(jobPost)
                .category(category)
                .build();

        jobPostCategoryRepository.save(jobPostCategory);
    }

    public void updateJobPostCategories(JobPost jobPost, Category newTaskCategory, Category newRegionCategory) {
        JobPostCategory taskJobPostCategory = findByJobPostAndCategoryType(jobPost, CategoryType.TASK);
        JobPostCategory regionJobPostCategory = findByJobPostAndCategoryType(jobPost, CategoryType.REGION);

        taskJobPostCategory.updateCategory(newTaskCategory);
        regionJobPostCategory.updateCategory(newRegionCategory);
    }
}
