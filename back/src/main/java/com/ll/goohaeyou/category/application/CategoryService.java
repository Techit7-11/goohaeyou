package com.ll.goohaeyou.category.application;

import com.ll.goohaeyou.category.application.dto.SubCategoryResponse;
import com.ll.goohaeyou.category.application.dto.TopLevelCategoryResponse;
import com.ll.goohaeyou.category.domain.CategoryDomainService;
import com.ll.goohaeyou.category.domain.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryDomainService categoryDomainService;

    public List<SubCategoryResponse> getSubCategories(Long categoryId) {
        List<Category> categories = categoryDomainService.getSubCategoriesById(categoryId);

        if (categories.isEmpty()) {
            return null;
        } else {
            return SubCategoryResponse.convertToList(categories);
        }
    }

    public List<TopLevelCategoryResponse> getTopCategories() {
        List<Category> categories = categoryDomainService.getTopCategories();

        return TopLevelCategoryResponse.convertToList(categories);
    }
}
