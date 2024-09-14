package com.ll.goohaeyou.chat.message.application.dto;

import jakarta.validation.constraints.NotBlank;

public record WriteMessageRequest(
        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        String content
) {}
