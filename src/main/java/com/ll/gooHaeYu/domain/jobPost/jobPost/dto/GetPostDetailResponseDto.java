package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import com.ll.gooHaeYu.domain.category.entity.Category;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetPostDetailResponseDto {
    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private Member member;

    private Category category;

    private String title;

    private String body;

    private boolean isClosed ;

    public GetPostDetailResponseDto(JobPost entity) {
        this.id = entity.getId();
        this.createdAt = entity.getCreatedAt();
        this.modifiedAt = entity.getModifiedAt();
        this.member = entity.getMember();
        this.category = entity.getCategory();
        this.title = entity.getTitle();
        this.body = entity.getBody();
        this.isClosed = entity.isClosed();
    }
}