package com.ll.goohaeyou.jobApplication.application.dto;

import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.member.member.domain.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

public record JobPostApplicationResponse(
        Long id,
        String jobPostAuthorUsername,
        Long jobPostId,
        String jobPostName,
        String author,
        String body,
        String wagePaymentMethod,
        LocalDateTime createdAt,
        String wageStatus,
        Boolean approve
) {
    public static JobPostApplicationResponse from(JobApplication jobApplication) {
        JobPostDetail jobPostDetail = jobApplication.getJobPostDetail();
        Member member = jobApplication.getMember();

        return new JobPostApplicationResponse(
                jobApplication.getId(),
                jobPostDetail.getAuthor(),
                jobPostDetail.getJobPost().getId(),
                jobPostDetail.getJobPost().getTitle(),
                member.getUsername(),
                jobApplication.getBody(),
                jobPostDetail.getWage().getWagePaymentMethod().getDescription(),
                jobApplication.getCreatedAt(),
                jobApplication.getWageStatus().getDescription(),
                jobApplication.getApprove()
        );
    }

    public static List<JobPostApplicationResponse> convertToList(List<JobApplication> jobApplications) {
        return jobApplications.stream()
                .map(JobPostApplicationResponse::from)
                .toList();
    }
}
