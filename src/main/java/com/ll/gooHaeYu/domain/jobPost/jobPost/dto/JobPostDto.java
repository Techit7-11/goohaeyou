package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.questionItem.dto.QuestionItemDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class JobPostDto {

    private Long JobPostId;
    private String author;
    private String categoryName;
    private String title;
    private String body;
    private List<QuestionItemDto> questionItems;
    private boolean isClosed;
    private LocalDateTime createdAt;

    public static JobPostDto fromEntity(JobPost jobPost) {
        List<QuestionItemDto> itemDtos = jobPost.getQuestionItems().stream()
                .map(QuestionItemDto::fromEntity)
                .toList();

        return JobPostDto.builder()
                .JobPostId(jobPost.getId())
                .author(jobPost.getMember().getUsername())
                .categoryName(jobPost.getCategory().getName())
                .title(jobPost.getTitle())
                .body(jobPost.getBody())
                .questionItems(itemDtos)
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
