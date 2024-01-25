package com.ll.gooHaeYu.domain.application.application.dto;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ApplicationDto {

    private Long id;
    private String author;
    private Long postId;
    private String body;
    private LocalDateTime createdAt;
    private boolean approve;

    public static ApplicationDto fromEntity(Application application) {
        return ApplicationDto.builder()
                .id(application.getId())
                .author(application.getMember().getUsername())
                .postId(application.getJobPost().getId())
                .body(application.getBody())
                .createdAt(application.getCreatedAt())
                .approve(application.isApprove())
                .build();
    }
}
