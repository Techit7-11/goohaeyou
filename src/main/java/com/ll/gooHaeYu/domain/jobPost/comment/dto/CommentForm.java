package com.ll.gooHaeYu.domain.jobPost.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CommentForm {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Register {
        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        private String content;
    }
}
