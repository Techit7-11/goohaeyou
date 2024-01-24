package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class JobPostForm {

    @Getter
    @Setter
    public static class Register {
        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        private String body;

        private List<QuestionItemForm> questionItemForms = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Modify {
        private String title;
        private String body;
        private Boolean closed;
    }
}