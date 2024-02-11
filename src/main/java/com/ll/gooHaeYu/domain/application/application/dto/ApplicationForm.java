package com.ll.gooHaeYu.domain.application.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class ApplicationForm {

    @Getter
    @Setter
    public static class Register {
        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        private String body;
    }

    @Getter
    @Setter
    public static class Modify {
        private String body;
    }
}
