package com.ll.goohaeyou.category.application;

import com.ll.goohaeyou.category.domain.Category;
import com.ll.goohaeyou.category.domain.repository.CategoryRepository;
import com.ll.goohaeyou.category.application.dto.CategoryDto;
import com.ll.goohaeyou.category.exception.CategoryException;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getSubCategories(String categoryName) {
        Category currentCategory = categoryRepository.findByName(categoryName)
                .orElseThrow(EntityNotFoundException.NotFoundCategoryException::new);

        List<Category> categories = currentCategory.getSubCategories();

        if (categories.isEmpty()) {
            return null;
        } else {
            return CategoryDto.convertToDtoList(categories);
        }
    }

    public boolean isLeafCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(EntityNotFoundException.NotFoundCategoryException::new);

        return category.isLeaf();
    }

    public List<CategoryDto> getTopCategories() {
        List<Category> categories = categoryRepository.findAllByLevel(1);

        return CategoryDto.convertToDtoList(categories);
    }
}
