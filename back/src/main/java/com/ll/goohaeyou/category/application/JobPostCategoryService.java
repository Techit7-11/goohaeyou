package com.ll.goohaeyou.category.application;

import com.ll.goohaeyou.category.domain.Category;
import com.ll.goohaeyou.category.domain.JobPostCategory;
import com.ll.goohaeyou.category.domain.repository.CategoryRepository;
import com.ll.goohaeyou.category.domain.repository.JobPostCategoryRepository;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.standard.base.RegionType;
import com.ll.goohaeyou.global.standard.base.util.Util;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostCategoryService {
    private final JobPostCategoryRepository jobPostCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final JobPostRepository jobPostRepository;

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
}
