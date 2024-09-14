package com.ll.goohaeyou.jobApplication.application.dto;

import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.member.member.domain.entity.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record JobApplicationDetailResponse(
        String author,
        String name,
        LocalDate birth,
        String phone,
        String location,
        Long id,
        LocalDateTime createdAt,
        Boolean approve,
        String body,
        String wageStatus,
        String jobPostAuthorUsername,
        int wages
) {
    public static JobApplicationDetailResponse from(JobApplication jobApplication) {
        JobPostDetail jobPostDetail = jobApplication.getJobPostDetail();
        Member member = jobApplication.getMember();

        return new JobApplicationDetailResponse(
                member.getUsername(),
                member.getName(),
                member.getBirth(),
                member.getPhoneNumber(),
                member.getLocation(),
                jobApplication.getId(),
                jobApplication.getCreatedAt(),
                jobApplication.getApprove(),
                jobApplication.getBody(),
                jobApplication.getWageStatus().getDescription(),
                jobPostDetail.getAuthor(),
                jobPostDetail.getWage().getCost()
        );
    }
}
