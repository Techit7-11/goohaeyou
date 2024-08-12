package com.ll.goohaeyou.category.application;

import com.ll.goohaeyou.category.domain.Category;
import com.ll.goohaeyou.category.domain.repository.CategoryRepository;
import com.ll.goohaeyou.category.dto.CategoryDto;
import com.ll.goohaeyou.category.exception.CategoryException;
import com.ll.goohaeyou.global.standard.base.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Category getCategoryByName(String categoryName) {

        return categoryRepository.findByName(categoryName)
                .orElseThrow(CategoryException.NotFoundCategoryException::new);
    }

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(CategoryException.NotFoundCategoryException::new);
    }

    public boolean isLeafCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryException.NotFoundCategoryException::new);

        return category.isLeaf();
    }

    public List<CategoryDto> getTopCategories() {
        List<Category> categories = categoryRepository.findAllByLevel(1);

        return CategoryDto.convertToDtoList(categories);
    }
}
