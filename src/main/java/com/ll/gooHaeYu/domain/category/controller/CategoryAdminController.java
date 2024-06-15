package com.ll.gooHaeYu.domain.category.controller;

import com.ll.gooHaeYu.domain.category.dto.CategoryForm;
import com.ll.gooHaeYu.domain.category.service.CategoryAdminService;
import com.ll.gooHaeYu.global.apiResponse.ApiResponse;
import com.ll.gooHaeYu.global.security.MemberDetails;
import com.ll.gooHaeYu.standard.base.Empty;
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
public class CategoryAdminController {
    private final CategoryAdminService categoryAdminService;

    @PostMapping("add")
    public ApiResponse<Empty> addCategory(@AuthenticationPrincipal MemberDetails memberDetail,
                                          @Valid @RequestBody CategoryForm.Add form) {
        categoryAdminService.addCategory(memberDetail.getUsername(), form);

        return ApiResponse.created();
    }
}
