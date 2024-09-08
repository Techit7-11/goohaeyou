package com.ll.goohaeyou.jobPost.jobPost.application.dto;

import com.ll.goohaeyou.jobPost.jobPost.domain.JobPost;

import java.util.List;

public record MyPostResponse(
        Long id,
        String title
) {
    public static MyPostResponse from(JobPost jobPost) {
        return new MyPostResponse(
                jobPost.getId(),
                jobPost.getTitle()
        );
    }

    public static List<MyPostResponse> convertToList(List<JobPost> jobPosts) {
        return jobPosts.stream()
                .map(MyPostResponse::from)
                .toList();
    }
}
