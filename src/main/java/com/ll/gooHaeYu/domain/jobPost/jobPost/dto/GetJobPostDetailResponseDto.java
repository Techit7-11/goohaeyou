package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import com.ll.gooHaeYu.domain.category.entity.Category;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetJobPostDetailResponseDto {

    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private String member;

    private Category category;

    private String title;

    private String body;

    private boolean isClosed;

    public GetJobPostDetailResponseDto(JobPost entity) {
        this.id = entity.getId();
        this.createdAt = entity.getCreatedAt();
        this.modifiedAt = entity.getModifiedAt();
        this.member = entity.getMember().getUsername();
        this.category = entity.getCategory();
        this.title = entity.getTitle();
        this.body = entity.getBody();
        this.isClosed = entity.isClosed();
    }
}