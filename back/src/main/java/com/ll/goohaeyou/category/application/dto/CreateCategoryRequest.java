package com.ll.goohaeyou.category.application.dto;

import com.ll.goohaeyou.category.domain.type.CategoryType;

public record CreateCategoryRequest(
        String parentName,
        String name,
        int level,
        CategoryType type,
        boolean enabled
) {}
