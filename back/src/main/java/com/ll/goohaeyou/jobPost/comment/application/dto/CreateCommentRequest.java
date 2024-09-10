package com.ll.goohaeyou.jobPost.comment.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(
        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        String content
) {}
