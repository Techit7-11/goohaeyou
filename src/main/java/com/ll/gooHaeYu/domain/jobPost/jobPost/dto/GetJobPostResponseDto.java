package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import com.ll.gooHaeYu.domain.category.entity.Category;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetJobPostResponseDto {

    // TODO 안쓰는 필드 삭제
    private Long id;

    private LocalDateTime createdAt;

    private String member;

    private Category category;

    private String title;

    private String body;

    private boolean isClosed;

    public GetJobPostResponseDto(JobPost entity) {
        this.id = entity.getId();
        this.createdAt = entity.getCreatedAt();
        this.member = entity.getMember().getUsername();
        this.category = entity.getCategory();
        this.title = entity.getTitle();
        this.body = entity.getBody();
        this.isClosed = entity.isClosed();
    }
}