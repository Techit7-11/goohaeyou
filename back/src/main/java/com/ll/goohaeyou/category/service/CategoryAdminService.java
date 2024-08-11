package com.ll.goohaeyou.category.service;

import com.ll.goohaeyou.category.domain.Category;
import com.ll.goohaeyou.category.dto.CategoryForm;
import com.ll.goohaeyou.category.domain.repository.CategoryRepository;
import com.ll.goohaeyou.global.exception.auth.AuthException;
import com.ll.goohaeyou.global.exception.category.CategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryAdminService {
    private static final String ADMIN_USERNAME = "admin";

    private final CategoryRepository categoryRepository;

    @Transactional
    public void addCategory(String username, CategoryForm.Add form) {
        if (!username.equals(ADMIN_USERNAME)) {
            throw new AuthException.NotAuthorizedException();
        }

        Category parent = getParent(form.getParentName());

        if (parent == null && form.getLevel() > 1) {
            throw new CategoryException.InvalidCategoryFormatException();
        }

        Category newCategory = Category.builder()
                .name(form.getName())
                .level(form.getLevel())
                .enabled(form.isEnabled())
                .parent(parent)
                .type(form.getType())
                .build();

        categoryRepository.save(newCategory);
    }

    private Category getParent(String name) {
        return categoryRepository.findByName(name)
                .orElse(null);
    }
}
