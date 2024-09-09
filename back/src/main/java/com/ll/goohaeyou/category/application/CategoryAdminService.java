package com.ll.goohaeyou.category.application;

import com.ll.goohaeyou.category.domain.Category;
import com.ll.goohaeyou.category.application.dto.CreateCategoryRequest;
import com.ll.goohaeyou.category.domain.repository.CategoryRepository;
import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.category.exception.CategoryException;
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
    public void addCategory(String username, CreateCategoryRequest request) {
        if (!username.equals(ADMIN_USERNAME)) {
            throw new AuthException.NotAuthorizedException();
        }

        Category parent = getParent(request.parentName());

        if (parent == null && request.level() > 1) {
            throw new CategoryException.InvalidCategoryFormatException();
        }

        Category newCategory = Category.create(request.name(), request.level(), request.enabled(), request.type(), parent);

        categoryRepository.save(newCategory);
    }

    public Category getParent(String name) {
        return categoryRepository.findByName(name)
                .orElse(null);
    }
}