package com.ll.goohaeyou.domain.mapper;

import com.ll.goohaeyou.domain.review.dto.ApplicantReviewDto;
import com.ll.goohaeyou.domain.review.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "jobPostingId", source = "jobPostingId.id")
    @Mapping(target = "applicantId", source = "applicantId.id")
    ApplicantReviewDto toApplicantReviewDto(Review review);

    @Mapping(target = "jobPostingId", ignore = true)
    @Mapping(target = "applicantId", ignore = true)
    Review toReviewEntity(ApplicantReviewDto applicantReviewDto);
}
