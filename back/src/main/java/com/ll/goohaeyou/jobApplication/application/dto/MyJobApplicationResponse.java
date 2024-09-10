package com.ll.goohaeyou.jobApplication.application.dto;

import com.ll.goohaeyou.jobApplication.domain.JobApplication;

import java.util.List;

public record MyJobApplicationResponse(
        Long id,
        String jobPostName,
        String body
) {
    public static MyJobApplicationResponse from(JobApplication jobApplication) {
        return new MyJobApplicationResponse(
                jobApplication.getId(),
                jobApplication.getJobPostDetail().getJobPost().getTitle(),
                jobApplication.getBody()
        );
    }

    public static List<MyJobApplicationResponse> convertToList(List<JobApplication> jobApplications) {
        return jobApplications.stream()
                .map(MyJobApplicationResponse::from)
                .toList();
    }
}
