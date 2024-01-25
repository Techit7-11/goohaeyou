package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionItemForm {
    @NotBlank(message = "문항의 내용은 공백일 수 없습니다.")
    private String content;
}
