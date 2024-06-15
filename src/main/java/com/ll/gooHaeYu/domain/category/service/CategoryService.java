package com.ll.gooHaeYu.domain.category.service;

import com.ll.gooHaeYu.domain.category.dto.CategoryDto;
import com.ll.gooHaeYu.domain.category.entity.Category;
import com.ll.gooHaeYu.domain.category.repository.CategoryRepository;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ll.gooHaeYu.global.exception.ErrorCode.NOT_FOUND_CATEGORY;

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
            return CategoryDto.toList(categories);
        }
    }

    private Category getCategoryByName(String categoryName) {

        return categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new CustomException(NOT_FOUND_CATEGORY));
    }
}
