package com.ll.gooHaeYu.domain.application.application.dto;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class ApplicationDto {

    @NotNull
    private Long id;
    @NotNull
    private Long jobPostId;
    @NotNull
    private String jobPostName;
    @NotNull
    private String author;
    @NotNull
    private Long postId;
    @NotNull
    private String body;
    private LocalDateTime createdAt;
    private Boolean approve;

    public static ApplicationDto fromEntity(Application application) {
        return ApplicationDto.builder()
                .id(application.getId())
                .jobPostId(application.getJobPostDetail().getJobPost().getId())
                .jobPostName(application.getJobPostDetail().getJobPost().getTitle())
                .author(application.getMember().getUsername())
                .postId(application.getJobPostDetail().getJobPost().getId())
                .body(application.getBody())
                .createdAt(application.getCreatedAt())
                .approve(application.getApprove())
                .build();
    }

    public static List<ApplicationDto> toDtoList(List<Application> applications) {
        return applications.stream()
                .map(ApplicationDto::fromEntity)
                .toList();
    }
}
