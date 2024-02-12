package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import com.ll.gooHaeYu.domain.member.member.entity.type.Gender;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

public class JobPostForm {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Register {
        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        private String body;

        @NotBlank(message = "지역 설정은 필수입니다.")
        private String location;

        private int minAge;

        private Gender gender = Gender.UNDEFINED;

        @NotNull(message = "마감 기한은 필수입니다.")
        @FutureOrPresent
        private LocalDate deadLine;
    }

    @Builder
    @Getter
    @Setter
    public static class Modify {
        private String title;
        private String body;
        private int minAge;
        private Gender gender;
        private LocalDate deadLine;
    }
}