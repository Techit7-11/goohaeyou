package com.ll.goohaeyou.category.domain.service;

import com.ll.goohaeyou.category.application.dto.CreateCategoryRequest;
import com.ll.goohaeyou.category.domain.entity.Category;
import com.ll.goohaeyou.category.domain.repository.CategoryRepository;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.standard.anotations.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@DomainService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryDomainService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public void create(CreateCategoryRequest request, Category parent) {
        Category newCategory = Category.create(
                request.name(),
                request.level(),
                request.enabled(),
                request.type(),
                parent
        );
        categoryRepository.save(newCategory);
    }

    public Category getByName(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseThrow(EntityNotFoundException.NotFoundCategoryException::new);
    }

    public Category getParentByName(String name) {
        return categoryRepository.findByName(name).orElse(null);
    }

    public List<Category> getSubCategoriesById(Long categoryId) {
        Category currentCategory = categoryRepository.findById(categoryId)
                .orElseThrow(EntityNotFoundException.NotFoundCategoryException::new);

        return currentCategory.getSubCategories();
    }

    public List<Category> getTopCategories() {
        return categoryRepository.findAllByLevel(1);
    }
}
