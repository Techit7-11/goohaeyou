package com.ll.goohaeyou.domain.jobPost.jobPost.dto;

import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPost;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPostImage;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@SuperBuilder
@Getter
public class JobPostDto extends AbstractJobPostDto{
    private String mainImageUrl = null;

    public static JobPostDto from(JobPost jobPost) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm");

        Optional<String> mainImageUrl = jobPost.getJobPostDetail().getJobPostImages().stream()
                .filter(JobPostImage::isMainImage)
                .map(JobPostImage::getJobPostImageUrl)
                .findFirst();

        return JobPostDto.builder()
                .id(jobPost.getId())
                .author(jobPost.getMember().getUsername())
                .title(jobPost.getTitle())
                .location(jobPost.getLocation())
                .commentsCount(jobPost.getCommentsCount())
                .incrementViewCount(jobPost.getIncrementViewCount())
                .interestsCount(jobPost.getInterestsCount())
                .employed(jobPost.isEmployed())
                .deadLine(jobPost.getDeadline())
                .isClosed(jobPost.isClosed())
                .mainImageUrl(mainImageUrl.orElse(null))
                .createdAt(jobPost.getCreatedAt().format(formatter))
                .build();
    }

    public static List<JobPostDto> convertToDtoList(List<JobPost> jobPosts) {
        return jobPosts.stream()
                .map(JobPostDto::from)
                .toList();
    }


    public static Page<JobPostDto> convertToDtoPage(Page<JobPost> jobPosts) {
        List<JobPostDto> jobPostDtos = jobPosts
                .map(JobPostDto::from)
                .toList();

        return new PageImpl<>(jobPostDtos, PageRequest.of(jobPosts.getNumber(), jobPosts.getSize()), jobPosts.getTotalElements());
    }
}
