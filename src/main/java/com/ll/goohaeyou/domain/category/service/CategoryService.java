package com.ll.goohaeyou.domain.category.service;

import com.ll.goohaeyou.domain.category.dto.CategoryDto;
import com.ll.goohaeyou.domain.category.entity.Category;
import com.ll.goohaeyou.domain.category.entity.repository.CategoryRepository;
import com.ll.goohaeyou.global.exception.GoohaeyouException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ll.goohaeyou.global.exception.ErrorCode.NOT_FOUND_CATEGORY;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getSubCategories(String categoryName) {
        Category currentCategory = getCategoryByName(categoryName);

        List<Category> categories = currentCategory.getSubCategories();

        if (categories.isEmpty()) {
            return null;
        } else {
            return CategoryDto.convertToDtoList(categories);
        }
    }

    private Category getCategoryByName(String categoryName) {

        return categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new GoohaeyouException(NOT_FOUND_CATEGORY));
    }

    public boolean isLeafCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new GoohaeyouException(NOT_FOUND_CATEGORY));

        return category.getSubCategories().isEmpty();
    }

    public List<CategoryDto> getTopCategories() {
        List<Category> categories = categoryRepository.findAllByLevel(1);

        return CategoryDto.convertToDtoList(categories);
    }
}
