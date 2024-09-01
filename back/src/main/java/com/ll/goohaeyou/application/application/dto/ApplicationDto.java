package com.ll.goohaeyou.application.application.dto;

import com.ll.goohaeyou.application.domain.Application;
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
public class ApplicationDto {
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

    public static ApplicationDto from(Application application) {
        JobPostDetail jobPostDetail = application.getJobPostDetail();
        Member member = application.getMember();

        return ApplicationDto.builder()
                .id(application.getId())
                .jobPostId(jobPostDetail.getJobPost().getId())
                .jobPostAuthorUsername(jobPostDetail.getAuthor())
                .jobPostName(jobPostDetail.getJobPost().getTitle())
                .author(member.getUsername())
                .body(application.getBody())
                .name(member.getName())
                .birth(member.getBirth())
                .phone(member.getPhoneNumber())
                .location(member.getLocation())
                .createdAt(application.getCreatedAt())
                .approve(application.getApprove())
                .wageStatus(application.getWageStatus().getDescription())
                .wagePaymentMethod(jobPostDetail.getWage().getWagePaymentMethod().getDescription())
                .wages(jobPostDetail.getWage().getCost())
                .build();
    }

    public static List<ApplicationDto> convertToDtoList(List<Application> applications) {
        return applications.stream()
                .map(ApplicationDto::from)
                .toList();
    }
}
