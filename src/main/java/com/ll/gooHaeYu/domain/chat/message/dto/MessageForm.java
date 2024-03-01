package com.ll.gooHaeYu.domain.chat.message.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class MessageForm {
    @Getter
    @Setter
    public static class Register {
        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        private String content;
        // 기본 생성자 추가
        public Register() {}

        // JSON 데이터를 역직렬화할 생성자 추가
        public Register(String content) {
            this.content = content;
        }
    }

    @Getter
    @Setter
    public static class Modify {
        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        private String body;
    }
}
