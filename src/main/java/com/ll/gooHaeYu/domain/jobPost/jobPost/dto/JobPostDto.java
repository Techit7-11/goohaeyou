package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.format.DateTimeFormatter;
import java.util.List;

@SuperBuilder
@Getter
public class JobPostDto extends AbstractJobPostDto{

    public static JobPostDto fromEntity(JobPost jobPost) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm");
        return JobPostDto.builder()
                .id(jobPost.getId())
                .author(jobPost.getMember().getUsername())
                .title(jobPost.getTitle())
                .location(jobPost.getLocation())
                .commentsCount(jobPost.getCommentsCount())
                .incrementViewCount(jobPost.getIncrementViewCount())
                .deadLine(jobPost.getDeadline())
                .createdAt(jobPost.getCreatedAt().format(formatter))
                .build();
    }

    public static List<JobPostDto> toDtoList(List<JobPost> jobPosts) {
        return jobPosts.stream()
                .map(JobPostDto::fromEntity)
                .toList();
    }


    public static Page<JobPostDto> toDtoListPage(Page<JobPost> jobPosts) {
        List<JobPostDto> jobPostDto = jobPosts
                .map(JobPostDto::fromEntity)
                .toList();

        return new PageImpl<>(jobPostDto, PageRequest.of(jobPosts.getNumber(), jobPosts.getSize()), jobPosts.getTotalElements());
    }
}
