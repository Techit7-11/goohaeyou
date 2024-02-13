package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import com.ll.gooHaeYu.domain.member.member.entity.type.Gender;
<<<<<<< HEAD
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
=======
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
>>>>>>> main

import java.time.LocalDate;

public class JobPostForm {

    @Builder
    @Getter
<<<<<<< HEAD
    @NoArgsConstructor
    @AllArgsConstructor
=======
    @Setter
>>>>>>> main
    public static class Register {
        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        private String body;

        @NotBlank(message = "지역 설정은 필수입니다.")
        private String location;

        private int minAge;

        private Gender gender = Gender.UNDEFINED;

<<<<<<< HEAD
        @NotNull(message = "마감 기한은 필수입니다.")
        @FutureOrPresent
        private LocalDate deadLine;
=======
        private LocalDate deadLine;
//        private LocalDateTime deadLine;
>>>>>>> main
    }

    @Builder
    @Getter
<<<<<<< HEAD
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Modify {
        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        private String body;

        @NotBlank(message = "지역 설정은 필수입니다.")
        private String location;

        private int minAge;

        private Gender gender;

        @NotNull(message = "마감 기한은 필수입니다.")
        @FutureOrPresent
        private LocalDate deadLine;
=======
    @Setter
    public static class Modify {
        private String title;
        private String body;
        private int minAge;
        private Gender gender;
        private LocalDate deadLine;
//        private LocalDateTime deadLine;
>>>>>>> main
    }
}