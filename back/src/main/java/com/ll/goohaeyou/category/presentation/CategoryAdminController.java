package com.ll.goohaeyou.category.presentation;

import com.ll.goohaeyou.category.application.CategoryAdminService;
import com.ll.goohaeyou.category.dto.CategoryForm;
import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import com.ll.goohaeyou.auth.domain.MemberDetails;
import com.ll.goohaeyou.global.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/category")
@Tag(name = "Category", description = "카테고리 관리 API")
public class CategoryAdminController {
    private final CategoryAdminService categoryAdminService;

    @PostMapping("add")
    @Operation(summary = "카테고리 추가")
    public ApiResponse<Empty> addCategory(@AuthenticationPrincipal MemberDetails memberDetail,
                                          @Valid @RequestBody CategoryForm.Add form) {
        categoryAdminService.addCategory(memberDetail.getUsername(), form);

        return ApiResponse.created();
    }
}
