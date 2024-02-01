package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class JobPostForm {

    @Builder
    @Getter
    @Setter
    public static class Register {
        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        private String body;

        @NotBlank(message = "지역 설정은 필수입니다.")
        private String location;

        private Integer minAge;

        private Integer gender;
    }

    @Builder
    @Getter
    @Setter
    public static class Modify {
        private String title;
        private String body;
        private Integer minAge;
        private Integer gender;
        private Boolean closed;
    }
}