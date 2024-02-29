package com.ll.gooHaeYu.domain.mapper;

import com.ll.gooHaeYu.domain.member.review.dto.ReviewDto;
import com.ll.gooHaeYu.domain.member.review.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    ReviewDto toDto(Review review);
    Review toEntity(ReviewDto reviewDto);
}
