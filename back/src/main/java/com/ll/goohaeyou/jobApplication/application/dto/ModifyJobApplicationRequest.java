package com.ll.goohaeyou.jobApplication.application.dto;

import jakarta.validation.constraints.NotBlank;

public record ModifyJobApplicationRequest(
        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        String body
) {}
