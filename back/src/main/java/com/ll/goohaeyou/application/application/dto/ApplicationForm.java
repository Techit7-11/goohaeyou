package com.ll.goohaeyou.application.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class ApplicationForm {
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Register {
        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        private String body;
    }

    @Getter
    @Setter
    public static class Modify {
        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        private String body;
    }
}
