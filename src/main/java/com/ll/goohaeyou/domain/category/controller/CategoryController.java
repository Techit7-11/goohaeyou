package com.ll.goohaeyou.domain.category.controller;

import com.ll.goohaeyou.domain.category.dto.CategoryDto;
import com.ll.goohaeyou.domain.category.service.CategoryService;
import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "카테고리 API")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/top-level")
    @Operation(summary = "최상단 카테고리 목록 불러오기")
    public ApiResponse<List<CategoryDto>> getTopCategories() {
        return ApiResponse.ok(categoryService.getTopCategories());
    }

    @GetMapping("/sub-categories")
    @Operation(summary = "하위 카테고리 목록 불러오기")
    public ApiResponse<List<CategoryDto>> getSubCategories(@RequestParam("category_name") String categoryName) {
        return ApiResponse.ok(categoryService.getSubCategories(categoryName));
    }

    @GetMapping("/is-leaf")
    @Operation(summary = "가장 최하단의 카테고리인지 확인")
    public ApiResponse<Boolean> isLeafCategory(@RequestParam("category_id") Long categoryId) {
        return ApiResponse.ok(categoryService.isLeafCategory(categoryId));
    }
}
