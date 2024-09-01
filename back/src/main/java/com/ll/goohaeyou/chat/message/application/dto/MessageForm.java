package com.ll.goohaeyou.chat.message.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class MessageForm {
    @Getter
    @Setter
    public static class Register {
        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        private String content;

        public Register() {}

        public Register(String content) {
            this.content = content;
        }
    }

    @Getter
    @Setter
    public static class Modify {
        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        private String content;
    }
}
