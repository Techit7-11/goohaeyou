package com.ll.goohaeyou.jobPost.jobPost.application.dto;

import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;

import java.util.List;

public record InterestedJobPostResponse(
        Long id,
        String title
) {
    public static InterestedJobPostResponse from(JobPost jobPost) {
        return new InterestedJobPostResponse(
                jobPost.getId(),
                jobPost.getTitle()
        );
    }

    public static List<InterestedJobPostResponse> convertToList(List<JobPostDetail> jobPostDetails) {
        return jobPostDetails.stream()
                .map(JobPostDetail::getJobPost)
                .map(InterestedJobPostResponse::from)
                .toList();
    }
}
