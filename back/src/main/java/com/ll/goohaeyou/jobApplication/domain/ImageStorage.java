package com.ll.goohaeyou.jobApplication.domain;

import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageStorage {
    String upload(MultipartFile image);
    void deleteImageFromS3(String imageAddress);
    void deletePostImagesFromS3(List<JobPostImage> jobPostImages);
}
