package com.ll.gooHaeYu.domain.application.application.dto;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class ApplicationDto {

    @NotNull
    private Long id;
    @NotNull
    private Long jobPostId;
    @NotBlank
    private String jobPostAuthorUsername;
    @NotBlank
    private String jobPostName;
    @NotNull
    private int deposit;
    @NotBlank
    private String author;
    @NotNull
    private Long postId;
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
    private String depositStatus;

    private LocalDateTime createdAt;
    private Boolean approve;

    public static ApplicationDto fromEntity(Application application) {
        return ApplicationDto.builder()
                .id(application.getId())
                .jobPostId(application.getJobPostDetail().getJobPost().getId())
                .jobPostAuthorUsername(application.getJobPostDetail().getAuthor())
                .jobPostName(application.getJobPostDetail().getJobPost().getTitle())
                .deposit(application.getJobPostDetail().getDeposit())
                .author(application.getMember().getUsername())
                .postId(application.getJobPostDetail().getJobPost().getId())
                .body(application.getBody())
                .name(application.getMember().getName())
                .birth(application.getMember().getBirth())
                .phone(application.getMember().getPhoneNumber())
                .location(application.getMember().getLocation())
                .createdAt(application.getCreatedAt())
                .approve(application.getApprove())
                .depositStatus(application.getDepositStatus().getDescription())
                .build();
    }

    public static List<ApplicationDto> toDtoList(List<Application> applications) {
        return applications.stream()
                .map(ApplicationDto::fromEntity)
                .toList();
    }
}
