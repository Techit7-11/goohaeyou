package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class JobPostDto {

    private Long id;
    private String author;
    private String categoryName;
    private String title;
    private String body;
    private List<QuestionItemDto> questionItems;
    private boolean isClosed;
    private LocalDateTime createdAt;

    public static JobPostDto fromEntity(JobPost jobPost) {
        return JobPostDto.builder()
                .id(jobPost.getId())
                .author(jobPost.getMember().getUsername())
                .categoryName(jobPost.getCategory().getName())
                .title(jobPost.getTitle())
                .body(jobPost.getBody())
                .isClosed(jobPost.isClosed())
                .createdAt(jobPost.getCreatedAt())
                .build();
    }

    public static List<JobPostDto> toDtoList(List<JobPost> jobPosts) {
        return jobPosts.stream()
                .map(JobPostDto::fromEntity)
                .toList();
    }
}
