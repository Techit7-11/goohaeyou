package com.ll.goohaeyou.jobPost.jobPost.application.dto;

import com.ll.goohaeyou.jobPost.jobPost.domain.entity.Essential;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.Wage;
import com.ll.goohaeyou.jobPost.jobPost.domain.type.PayBasis;
import com.ll.goohaeyou.jobPost.jobPost.domain.type.WagePaymentMethod;
import com.ll.goohaeyou.member.member.domain.type.Gender;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record JobPostDetailResponse(
        String author,
        String createdAt,
        boolean closed,
        Long id,
        String title,
        long incrementViewCount,
        long interestsCount,
        int cost,
        PayBasis payBasis,
        int workTime,
        int workDays,
        WagePaymentMethod wagePaymentMethod,
        LocalDate jobStartDate,
        boolean employed,
        LocalDate deadLine,
        int minAge,
        Gender gender,
        String location,
        String body
) {
    public static JobPostDetailResponse from(JobPost jobPost, JobPostDetail jobPostDetail, Essential essential, Wage wage) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm");

        return new JobPostDetailResponse(
                jobPostDetail.getAuthor(),
                jobPost.getCreatedAt().format(formatter),
                jobPost.isClosed(),
                jobPost.getId(),
                jobPost.getTitle(),
                jobPost.getIncrementViewCount(),
                jobPost.getInterestsCount(),
                wage.getCost(),
                wage.getPayBasis(),
                wage.getWorkTime(),
                wage.getWorkDays(),
                wage.getWagePaymentMethod(),
                jobPost.getJobStartDate(),
                jobPost.isEmployed(),
                jobPost.getDeadline(),
                essential.getMinAge(),
                essential.getGender(),
                jobPost.getLocation(),
                jobPostDetail.getBody()
        );
    }
}
