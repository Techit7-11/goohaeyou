package com.ll.goohaeyou.category.presentation;

import com.ll.goohaeyou.category.application.CategoryService;
import com.ll.goohaeyou.category.application.dto.SubCategoryResponse;
import com.ll.goohaeyou.category.application.dto.TopLevelCategoryResponse;
import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "카테고리 API")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/top-level")
    @Operation(summary = "최상단 카테고리 목록 불러오기")
    public ApiResponse<List<TopLevelCategoryResponse>> getTopCategories() {
        return ApiResponse.ok(categoryService.getTopCategories());
    }

    @GetMapping("/{categoryId}/sub-categories")
    @Operation(summary = "하위 카테고리 목록 불러오기")
    public ApiResponse<List<SubCategoryResponse>> getSubCategories(@PathVariable("categoryId") Long categoryId) {
        return ApiResponse.ok(categoryService.getSubCategories(categoryId));
    }
}
