package com.ll.goohaeyou.jobPost.jobPost.domain;

import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.jobApplication.domain.ImageStorage;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostImage;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@DomainService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostImageDomainService {
    private final ImageStorage imageStorage;

    public void deleteImages(JobPost jobPost) {
        List<JobPostImage> jobPostImages = jobPost.getJobPostDetail().getJobPostImages();
        if (!jobPostImages.isEmpty()) {
            imageStorage.deletePostImagesFromS3(jobPostImages);
        }
    }
}
