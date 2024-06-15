package com.ll.gooHaeYu.domain.category.controller;

import com.ll.gooHaeYu.domain.category.dto.CategoryDto;
import com.ll.gooHaeYu.domain.category.service.CategoryService;
import com.ll.gooHaeYu.global.apiResponse.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/sub-categories")
    public ApiResponse<List<CategoryDto>> getSubCategories(@RequestParam("category_name") String categoryName) {
        return ApiResponse.ok(categoryService.getSubCategories(categoryName));
    }
}
