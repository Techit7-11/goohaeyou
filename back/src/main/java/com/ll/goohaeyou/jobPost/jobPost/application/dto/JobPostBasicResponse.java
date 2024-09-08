package com.ll.goohaeyou.jobPost.jobPost.application.dto;

import com.ll.goohaeyou.jobPost.jobPost.domain.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public record JobPostBasicResponse(
        Long id,
        String author,
        String location,
        String mainImageUrl,
        String title,
        long incrementViewCount,
        long commentsCount,
        long interestsCount,
        boolean closed,
        LocalDate deadLine
) {
    public static JobPostBasicResponse from(JobPost jobPost) {
        Optional<String> mainImageUrl = jobPost.getJobPostDetail().getJobPostImages().stream()
                .filter(JobPostImage::isMainImage)
                .map(JobPostImage::getJobPostImageUrl)
                .findFirst();

        return new JobPostBasicResponse(
                jobPost.getId(),
                jobPost.getMember().getUsername(),
                jobPost.getLocation(),
                mainImageUrl.orElse(null),
                jobPost.getTitle(),
                jobPost.getIncrementViewCount(),
                jobPost.getCommentsCount(),
                jobPost.getInterestsCount(),
                jobPost.isClosed(),
                jobPost.getDeadline()
        );
    }

    public static List<JobPostBasicResponse> convertToList(List<JobPost> jobPosts) {
        return jobPosts.stream()
                .map(JobPostBasicResponse::from)
                .toList();
    }

    public static Page<JobPostBasicResponse> convertToPage(Page<JobPost> jobPosts) {
        List<JobPostBasicResponse> JobPostBasicResponseList = jobPosts
                .map(JobPostBasicResponse::from)
                .toList();

        return new PageImpl<>(JobPostBasicResponseList, PageRequest.of(jobPosts.getNumber(), jobPosts.getSize()), jobPosts.getTotalElements());
    }
}
