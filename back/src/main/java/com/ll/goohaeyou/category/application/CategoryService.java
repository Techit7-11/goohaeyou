package com.ll.goohaeyou.category.application;

import com.ll.goohaeyou.category.application.dto.SubCategoryResponse;
import com.ll.goohaeyou.category.application.dto.TopLevelCategoryResponse;
import com.ll.goohaeyou.category.domain.entity.Category;
import com.ll.goohaeyou.category.domain.repository.CategoryRepository;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<SubCategoryResponse> getSubCategories(Long categoryId) {
        Category currentCategory = categoryRepository.findById(categoryId)
                .orElseThrow(EntityNotFoundException.NotFoundCategoryException::new);

        List<Category> categories = currentCategory.getSubCategories();

        if (categories.isEmpty()) {
            return null;
        } else {
            return SubCategoryResponse.convertToList(categories);
        }
    }

    public List<TopLevelCategoryResponse> getTopCategories() {
        List<Category> categories = categoryRepository.findAllByLevel(1);

        return TopLevelCategoryResponse.convertToList(categories);
    }
}
