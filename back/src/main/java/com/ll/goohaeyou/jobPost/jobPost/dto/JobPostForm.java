package com.ll.goohaeyou.jobPost.jobPost.dto;

import com.ll.goohaeyou.jobPost.jobPost.domain.type.PayBasis;
import com.ll.goohaeyou.jobPost.jobPost.domain.type.WagePaymentMethod;
import com.ll.goohaeyou.member.member.domain.type.Gender;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class JobPostForm {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Register {
        @NotNull(message = "카테고리 설정은 필수입니다.")
        private Long categoryId;

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

        @NotNull(message = "시작 일자 설정은 필수입니다.")
        @FutureOrPresent
        private LocalDate jobStartDate;

        // 프론트에서 유효성 검사
        private int workTime;
        private int workDays;

        private PayBasis payBasis;

        @NotNull(message = "급여 입력은 필수 입니다.")
        @Min(value = 0, message = "급여는 0원 이상 이여야 합니다.")
        private int cost;

        private WagePaymentMethod wagePaymentMethod;

        public Register(String title, String body, String location, int minAge, Gender gender, LocalDate deadLine, LocalDate jobStartDate, int workTime, int workDays, PayBasis payBasis, int cost, WagePaymentMethod wagePaymentMethod) {
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Modify {
        @NotNull(message = "카테고리 설정은 필수입니다.")
        private Long categoryId;

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

        @NotNull(message = "시작 일자 설정은 필수입니다.")
        @FutureOrPresent
        private LocalDate jobStartDate;

        // 프론트에서 유효성 검사
        private int workTime;
        private int workDays;

        private PayBasis payBasis;

        @NotNull(message = "급여 입력은 필수 입니다.")
        @Min(value = 0, message = "급여는 0원 이상 이여야 합니다.")
        private int cost;
    }
}
