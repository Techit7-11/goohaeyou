package com.ll.goohaeyou.category.application;

import com.ll.goohaeyou.category.application.dto.CreateCategoryRequest;
import com.ll.goohaeyou.category.domain.service.CategoryDomainService;
import com.ll.goohaeyou.category.domain.entity.Category;
import com.ll.goohaeyou.category.domain.policy.CategoryPolicy;
import com.ll.goohaeyou.member.member.domain.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryAdminService {
    private final CategoryPolicy categoryPolicy;
    private final CategoryDomainService categoryDomainService;

    @Transactional
    public void addCategory(Role role, CreateCategoryRequest request) {
        Category parent = categoryDomainService.getParentByName(request.parentName());

        categoryPolicy.validateCategoryCreation(role, parent, request.level());

        categoryDomainService.create(request, parent);
    }
}
