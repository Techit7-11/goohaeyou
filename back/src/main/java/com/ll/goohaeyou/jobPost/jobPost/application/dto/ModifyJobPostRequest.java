package com.ll.goohaeyou.jobPost.jobPost.application.dto;

import com.ll.goohaeyou.jobPost.jobPost.domain.type.PayBasis;
import com.ll.goohaeyou.member.member.domain.type.Gender;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ModifyJobPostRequest(
        @NotNull(message = "카테고리 설정은 필수입니다.")
        Long categoryId,

        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        String title,

        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        String body,

        @NotBlank(message = "지역 설정은 필수입니다.")
        String location,

        int minAge,

        Gender gender,

        @NotNull(message = "마감 기한은 필수입니다.")
        @FutureOrPresent
        LocalDate deadLine,

        @NotNull(message = "시작 일자 설정은 필수입니다.")
        @FutureOrPresent
        LocalDate jobStartDate,

        // 프론트에서 유효성 검사
        int workDays,
        int workTime,

        PayBasis payBasis,

        @NotNull(message = "대금 입력은 필수 입니다.")
        @Min(value = 0, message = "대금은 0원 이상 이여야 합니다.")
        int cost
    ) {}
