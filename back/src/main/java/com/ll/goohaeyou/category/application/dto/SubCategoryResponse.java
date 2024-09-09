package com.ll.goohaeyou.category.application.dto;

import com.ll.goohaeyou.category.domain.Category;

import java.util.List;

public record SubCategoryResponse(
        String name
) {
    public static SubCategoryResponse from(Category category) {
        return new SubCategoryResponse(
                category.getName()
        );
    }

    public static List<SubCategoryResponse> convertToList(List<Category> categories) {
        return categories.stream()
                .map(SubCategoryResponse::from)
                .toList();
    }
}
