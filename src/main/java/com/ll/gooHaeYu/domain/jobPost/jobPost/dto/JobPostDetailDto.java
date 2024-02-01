package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.Essential;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.gooHaeYu.domain.member.member.entity.type.Gender;
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
    private int minAge;
    private Gender gender;
    private boolean isClosed = false; // 기본값 설정
    @NonNull
    private String createdAt;
    private String modifyAt;

    public static JobPostDetailDto fromEntity(JobPost jobPost, JobPostDetail jobPostDetail, Essential essential) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm");

        return JobPostDetailDto.builder()
                .id(jobPost.getId())
                .author(jobPost.getMember().getUsername())
                .title(jobPost.getTitle())
                .body(jobPostDetail.getBody())
                .minAge(essential.getMinAge())
                .gender(essential.getGender())
                .isClosed(jobPost.isClosed())
                .createdAt(jobPost.getCreatedAt().format(formatter))
                .modifyAt(jobPost.getModifiedAt().format(formatter))
                .build();
    }
}
