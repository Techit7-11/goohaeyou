package com.ll.goohaeyou.jobPost.jobPost.application.dto;

import com.ll.goohaeyou.jobPost.jobPost.domain.*;
import com.ll.goohaeyou.jobPost.jobPost.domain.type.PayBasis;
import com.ll.goohaeyou.jobPost.jobPost.domain.type.WagePaymentMethod;
import com.ll.goohaeyou.member.member.domain.type.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class JobPostDetailDto {
    @NotNull
    private Long id;
    @NotNull
    private String author;
    @NotNull
    private String title;
    @NotNull
    private String location;
    @NotNull
    private long commentsCount;
    @NotNull
    private long incrementViewCount;
    @NotNull
    private long interestsCount;
    @NotNull
    private String createdAt;

    private boolean employed;
    private boolean isClosed;
    private LocalDate deadLine;
    private LocalDate jobStartDate;

    @NotNull
    private String body;
    private long applicationCount;
    private int minAge;
    private Gender gender;
    private String modifiedAt;
    private int workTime;
    private int workDays;
    private int cost;
    private PayBasis payBasis;
    private WagePaymentMethod wagePaymentMethod;

    public static JobPostDetailDto from(JobPost jobPost, JobPostDetail jobPostDetail, Essential essential, Wage wage) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm");
        LocalDateTime mostRecentModifiedDate = jobPost.getModifiedAt().isAfter(jobPostDetail.getModifiedAt()) ? jobPost.getModifiedAt() : jobPostDetail.getModifiedAt();

        return JobPostDetailDto.builder()
                .id(jobPost.getId())
                .author(jobPostDetail.getAuthor())
                .title(jobPost.getTitle())
                .body(jobPostDetail.getBody())
                .location(jobPost.getLocation())
                .incrementViewCount(jobPost.getIncrementViewCount())
                .commentsCount(jobPost.getCommentsCount())
                .applicationCount(jobPost.getApplicationCount())
                .interestsCount(jobPost.getInterestsCount())
                .gender(essential.getGender())
                .minAge(essential.getMinAge())
                .deadLine(jobPost.getDeadline())
                .jobStartDate(jobPost.getJobStartDate())
                .isClosed(jobPost.isClosed())
                .employed(jobPost.isEmployed())
                .cost(wage.getCost())
                .payBasis(wage.getPayBasis())
                .workTime(wage.getWorkTime())
                .workDays(wage.getWorkDays())
                .wagePaymentMethod(wage.getWagePaymentMethod())
                .createdAt(jobPost.getCreatedAt().format(formatter))
                .modifiedAt(mostRecentModifiedDate.format(formatter))
                .build();
    }
}
