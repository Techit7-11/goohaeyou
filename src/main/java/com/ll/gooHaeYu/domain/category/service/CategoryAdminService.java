package com.ll.gooHaeYu.domain.category.service;

import com.ll.gooHaeYu.domain.category.dto.CategoryForm;
import com.ll.gooHaeYu.domain.category.entity.Category;
import com.ll.gooHaeYu.domain.category.repository.CategoryRepository;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ll.gooHaeYu.global.exception.ErrorCode.NOT_ABLE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryAdminService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public void addCategory(String username, CategoryForm.Add form) {
        if (!username.equals("admin")) {
            throw new CustomException(NOT_ABLE);
        }

        Category newCategory = Category.builder()
                .name(form.getName())
                .code(form.getCode())
                .level(form.getLevel())
                .enabled(form.isEnabled())
                .build();

        categoryRepository.save(newCategory);
    }
}
