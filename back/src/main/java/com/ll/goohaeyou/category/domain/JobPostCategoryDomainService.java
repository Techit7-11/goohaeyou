package com.ll.goohaeyou.category.domain;

import com.ll.goohaeyou.category.domain.entity.Category;
import com.ll.goohaeyou.category.domain.entity.JobPostCategory;
import com.ll.goohaeyou.category.domain.repository.CategoryRepository;
import com.ll.goohaeyou.category.domain.repository.JobPostCategoryRepository;
import com.ll.goohaeyou.category.domain.type.CategoryType;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.global.standard.base.RegionType;
import com.ll.goohaeyou.global.standard.base.util.Util;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.ModifyJobPostRequest;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@DomainService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostCategoryDomainService {
    private final JobPostCategoryRepository jobPostCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final JobPostRepository jobPostRepository;

    @Transactional
    public void create(Long jobPostId, String Location, Long categoryId) {
        int regionCode = Util.Region.getRegionCodeFromAddress(Location);
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);

        Category taskCategory = categoryRepository.findById(categoryId)
                .orElseThrow(EntityNotFoundException.NotFoundCategoryException::new);
        Category regionCategory = categoryRepository.findByName(RegionType.getNameByCode(regionCode))
                .orElseThrow(EntityNotFoundException.NotFoundCategoryException::new);

        JobPostCategory newJobPostCategory1 = JobPostCategory.create(regionCategory, jobPost);
        JobPostCategory newJobPostCategory2 = JobPostCategory.create(taskCategory, jobPost);

        jobPostCategoryRepository.save(newJobPostCategory1);
        jobPostCategoryRepository.save(newJobPostCategory2);
    }

    @Transactional
    public void update(JobPost jobPost, ModifyJobPostRequest request, int newRegionCode) {
        JobPostCategory existingTaskJobPostCategory = jobPostCategoryRepository.findByJobPostAndCategory_Type(jobPost, CategoryType.TASK);
        JobPostCategory existingRegionJobPostCategory = jobPostCategoryRepository.findByJobPostAndCategory_Type(jobPost, CategoryType.REGION);

        Category newTaskCategory = categoryRepository.findById(request.categoryId())
                .orElseThrow(EntityNotFoundException.NotFoundCategoryException::new);
        Category newRegionCategory = categoryRepository.findByName(RegionType.getNameByCode(newRegionCode))
                .orElseThrow(EntityNotFoundException.NotFoundCategoryException::new);

        existingTaskJobPostCategory.updateCategory(newTaskCategory);
        existingRegionJobPostCategory.updateCategory(newRegionCategory);
    }

    @Transactional
    public void deleteAll(JobPost jobPost) {
        jobPostCategoryRepository.deleteAllByJobPost(jobPost);
    }
}
