package com.ll.goohaeyou.category.application.dto;

import com.ll.goohaeyou.category.domain.Category;

import java.util.List;

public record TopLevelCategoryResponse(
        Long id,
        String name
) {
    public static TopLevelCategoryResponse from(Category category) {
        return new TopLevelCategoryResponse(
                category.getId(),
                category.getName()
        );
    }

    public static List<TopLevelCategoryResponse> convertToList(List<Category> categories) {
        return categories.stream()
                .map(TopLevelCategoryResponse::from)
                .toList();
    }
}
