package com.ll.goohaeyou.domain.category.dto;

import com.ll.goohaeyou.domain.category.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CategoryDto {
    private Long id;

    private String name;

    private int code;

    private int level;

    private Long parentId;

    public static CategoryDto from(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .level(category.getLevel())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .build();
    }

    public static List<CategoryDto> toList(List<Category> categories) {
        return categories.stream()
                .map(CategoryDto::from)
                .toList();
    }
}
