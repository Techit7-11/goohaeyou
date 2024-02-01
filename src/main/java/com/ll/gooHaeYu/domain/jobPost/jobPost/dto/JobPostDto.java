package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Builder
@Getter
public class JobPostDto {
    @NotNull
    private Long id;
    @NotNull
    private String author;
    @NotNull
    private String title;
    @NotNull
    private String location;
    private boolean isClosed = false; // 기본값 설정
    @NonNull
    private String createdAt;

    public static JobPostDto fromEntity(JobPost jobPost) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm");

        return JobPostDto.builder()
                .id(jobPost.getId())
                .author(jobPost.getMember().getUsername())
                .title(jobPost.getTitle())
                .location(jobPost.getLocation())
                .isClosed(jobPost.isClosed())
                .createdAt(jobPost.getCreatedAt().format(formatter))
                .build();
    }

    public static List<JobPostDto> toDtoList(List<JobPost> jobPosts) {
        return jobPosts.stream()
                .map(JobPostDto::fromEntity)
                .toList();
    }
}
