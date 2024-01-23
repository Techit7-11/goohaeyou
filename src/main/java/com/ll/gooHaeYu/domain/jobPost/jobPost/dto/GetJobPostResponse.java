package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import lombok.Getter;

@Getter
public class GetJobPostResponse {

    private final String title;
    private final String body;

    public GetJobPostResponse(JobPost jobPost) {
        this.title = jobPost.getTitle();
        this.body = jobPost.getBody();
    }
}