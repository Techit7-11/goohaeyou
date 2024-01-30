package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPostDetail;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Builder
@Getter
public class JobPostDetailDto {
    @NotNull
    private Long id;
    @NotNull
    private String author;
    @NotNull
    private String title;
    @NotNull
    private String body;
    private boolean isClosed = false; // 기본값 설정
    @NonNull
    private String createdAt;
    private String modifyAt;

    public static JobPostDetailDto fromEntity(JobPost jobPost,JobPostDetail jobPostDetail) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm");

        return JobPostDetailDto.builder()
                .id(jobPost.getId())
                .author(jobPost.getMember().getUsername())
                .title(jobPost.getTitle())
                .body(jobPostDetail.getBody())
                .isClosed(jobPost.isClosed())
                .createdAt(jobPost.getCreatedAt().format(formatter))
                .modifyAt(jobPost.getModifiedAt().format(formatter))
                .build();
    }
}
