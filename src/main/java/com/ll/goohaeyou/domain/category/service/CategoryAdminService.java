package com.ll.goohaeyou.domain.category.service;

import com.ll.goohaeyou.domain.category.dto.CategoryForm;
import com.ll.goohaeyou.domain.category.entity.Category;
import com.ll.goohaeyou.domain.category.entity.repository.CategoryRepository;
import com.ll.goohaeyou.global.exception.GoohaeyouException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ll.goohaeyou.global.exception.ErrorCode.INVALID_CATEGORY_FORMAT;
import static com.ll.goohaeyou.global.exception.ErrorCode.NOT_ABLE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryAdminService {
    private static final String ADMIN_USERNAME = "admin";

    private final CategoryRepository categoryRepository;

    @Transactional
    public void addCategory(String username, CategoryForm.Add form) {
        if (!username.equals(ADMIN_USERNAME)) {
            throw new GoohaeyouException(NOT_ABLE);
        }

        Category parent = getParent(form.getParentName());

        if (parent == null && form.getLevel() > 1) {
            throw new GoohaeyouException(INVALID_CATEGORY_FORMAT);
        }

        Category newCategory = Category.builder()
                .name(form.getName())
                .code(form.getCode())
                .level(form.getLevel())
                .enabled(form.isEnabled())
                .parent(parent)
                .build();

        categoryRepository.save(newCategory);
    }

    private Category getParent(String name) {
        return categoryRepository.findByName(name)
                .orElse(null);
    }
}
