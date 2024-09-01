package com.ll.goohaeyou.category.application.dto;

import com.ll.goohaeyou.category.domain.type.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

public class CategoryForm {
    @Builder
    @Getter
    public static class Add {

        private String parentName;
        @NotBlank
        private String name;
        @NotNull
        private int level = 0;

        private CategoryType type;
        private boolean enabled = true;
    }
}
