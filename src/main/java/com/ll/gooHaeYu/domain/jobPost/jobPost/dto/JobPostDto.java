package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
    @NotNull
    private long commentsCount;
    @NotNull
    private long applicationCount;
    @NotNull
    private long interestsCount;

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
                .commentsCount(jobPost.getCommentsCount())
                .applicationCount(jobPost.getApplicationCount())
                .interestsCount(jobPost.getInterestsCount())
                .isClosed(jobPost.isClosed())
                .createdAt(jobPost.getCreatedAt().format(formatter))
                .build();
    }

    public static List<JobPostDto> toDtoList(List<JobPost> jobPosts) {
        return jobPosts.stream()
                .map(JobPostDto::fromEntity)
                .toList();
    }


    public static Page<JobPostDto> toDtoListPage(Page<JobPost> jobPosts) {
        List<JobPostDto> jobPostDtos = jobPosts.stream()
                .map(JobPostDto::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(jobPostDtos, PageRequest.of(jobPosts.getNumber(), jobPosts.getSize()), jobPosts.getTotalElements());
    }
}
