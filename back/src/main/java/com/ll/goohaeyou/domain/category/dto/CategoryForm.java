package com.ll.goohaeyou.domain.category.dto;

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

        private boolean enabled = true;
    }
}
