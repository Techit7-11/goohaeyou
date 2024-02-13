package com.ll.gooHaeYu.domain.application.application.dto;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
<<<<<<< HEAD
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
=======
import lombok.Builder;
import lombok.Getter;

>>>>>>> main
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class ApplicationDto {

<<<<<<< HEAD
    @NotNull
    private Long id;
    @NotNull
    private Long jobPostId;
    @NotNull
    private String jobPostAuthorUsername;
    @NotNull
    private String jobPostName;
    @NotNull
    private String author;
    @NotNull
    private Long postId;
    @NotNull
    private String body;
    @NotNull
    private LocalDate birth;
    @NotNull
    private String phone;
    @NotNull
    private String location;
=======
    private Long id;
    private String author;
    private Long postId;
    private String body;
>>>>>>> main
    private LocalDateTime createdAt;
    private Boolean approve;

    public static ApplicationDto fromEntity(Application application) {
        return ApplicationDto.builder()
                .id(application.getId())
<<<<<<< HEAD
                .jobPostId(application.getJobPostDetail().getJobPost().getId())
                .jobPostAuthorUsername(application.getJobPostDetail().getAuthor())
                .jobPostName(application.getJobPostDetail().getJobPost().getTitle())
                .author(application.getMember().getUsername())
                .postId(application.getJobPostDetail().getJobPost().getId())
                .body(application.getBody())
                .birth(application.getMember().getBirth())
                .phone(application.getMember().getPhoneNumber())
                .location(application.getMember().getLocation())
=======
                .author(application.getMember().getUsername())
                .postId(application.getJobPostDetail().getJobPost().getId())
                .body(application.getBody())
>>>>>>> main
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
