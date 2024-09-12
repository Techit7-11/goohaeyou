package com.ll.goohaeyou.category.domain;

import com.ll.goohaeyou.category.domain.entity.Category;
import com.ll.goohaeyou.category.domain.repository.CategoryRepository;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.standard.anotations.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@DomainService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryDomainService {
    private final CategoryRepository categoryRepository;

    public Category getByName(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseThrow(EntityNotFoundException.NotFoundCategoryException::new);
    }
}
