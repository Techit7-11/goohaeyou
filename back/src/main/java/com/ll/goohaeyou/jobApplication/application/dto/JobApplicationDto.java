package com.ll.goohaeyou.jobApplication.application.dto;

import com.ll.goohaeyou.jobApplication.domain.JobApplication;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import com.ll.goohaeyou.member.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class JobApplicationDto {
    @NotNull
    private Long id;
    @NotBlank
    private String jobPostAuthorUsername;
    @NotNull
    private Long jobPostId;
    @NotBlank
    private String jobPostName;
    @NotBlank
    private String author;
    @NotBlank
    private String body;
    @NotBlank
    private String name;
    @NotNull
    private LocalDate birth;
    @NotBlank
    private String phone;
    @NotBlank
    private String location;
    @NotBlank
    private String wageStatus;
    @NotBlank
    private String wagePaymentMethod;
    @NotNull int wages = 0;

    private LocalDateTime createdAt;
    private Boolean approve;

    public static JobApplicationDto from(JobApplication jobApplication) {
        JobPostDetail jobPostDetail = jobApplication.getJobPostDetail();
        Member member = jobApplication.getMember();

        return JobApplicationDto.builder()
                .id(jobApplication.getId())
                .jobPostId(jobPostDetail.getJobPost().getId())
                .jobPostAuthorUsername(jobPostDetail.getAuthor())
                .jobPostName(jobPostDetail.getJobPost().getTitle())
                .author(member.getUsername())
                .body(jobApplication.getBody())
                .name(member.getName())
                .birth(member.getBirth())
                .phone(member.getPhoneNumber())
                .location(member.getLocation())
                .createdAt(jobApplication.getCreatedAt())
                .approve(jobApplication.getApprove())
                .wageStatus(jobApplication.getWageStatus().getDescription())
                .wagePaymentMethod(jobPostDetail.getWage().getWagePaymentMethod().getDescription())
                .wages(jobPostDetail.getWage().getCost())
                .build();
    }

    public static List<JobApplicationDto> convertToDtoList(List<JobApplication> jobApplications) {
        return jobApplications.stream()
                .map(JobApplicationDto::from)
                .toList();
    }
}
