package com.ll.goohaeyou.category.application.dto;

import com.ll.goohaeyou.category.domain.JobPostCategory;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobPostCategoryDto {
    @NotNull
    Long jobPostId;

    @NotNull
    Long categoryId;

    public static JobPostCategoryDto from(JobPostCategory jobPostCategory) {
        return JobPostCategoryDto.builder()
                .jobPostId(jobPostCategory.getCategory().getId())
                .categoryId(jobPostCategory.getCategory().getId())
                .build();
    }
}
