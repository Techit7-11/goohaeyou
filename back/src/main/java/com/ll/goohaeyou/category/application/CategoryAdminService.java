package com.ll.goohaeyou.category.application;

import com.ll.goohaeyou.category.domain.Category;
import com.ll.goohaeyou.category.application.dto.CreateCategoryRequest;
import com.ll.goohaeyou.category.domain.policy.CategoryPolicy;
import com.ll.goohaeyou.category.domain.repository.CategoryRepository;
import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.category.exception.CategoryException;
import com.ll.goohaeyou.member.member.domain.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryAdminService {
    private final CategoryRepository categoryRepository;
    private final CategoryPolicy categoryPolicy;

    @Transactional
    public void addCategory(Role role, CreateCategoryRequest request) {
        Category parent = getParent(request.parentName());

        categoryPolicy.validateCategoryCreation(role, parent, request.level());

        Category newCategory = Category.create(request.name(), request.level(), request.enabled(), request.type(), parent);

        categoryRepository.save(newCategory);
    }

    public Category getParent(String name) {
        return categoryRepository.findByName(name)
                .orElse(null);
    }
}
