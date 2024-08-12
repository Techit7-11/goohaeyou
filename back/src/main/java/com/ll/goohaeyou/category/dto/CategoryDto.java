package com.ll.goohaeyou.category.dto;

import com.ll.goohaeyou.category.domain.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CategoryDto {
    private Long id;
    private String name;
    private int level;
    private Long parentId;

    public static CategoryDto from(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .level(category.getLevel())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .build();
    }

    public static List<CategoryDto> convertToDtoList(List<Category> categories) {
        return categories.stream()
                .map(CategoryDto::from)
                .toList();
    }
}
