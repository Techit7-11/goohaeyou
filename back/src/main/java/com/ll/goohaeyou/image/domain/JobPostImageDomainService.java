package com.ll.goohaeyou.image.domain;

import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.image.exception.ImageException;
import com.ll.goohaeyou.jobApplication.domain.ImageStorage;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostImage;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostDetailRepository;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@DomainService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostImageDomainService {
    private final JobPostImageRepository jobPostImageRepository;
    private final JobPostDetailRepository jobPostDetailRepository;
    private final ImageStorage imageStorage;

    @Transactional
    public void addJobPostImages(JobPostDetail jobPostDetail, MultipartFile[] jobPostImageFiles) {
        List<JobPostImage> jobPostImages = new ArrayList<>();
        boolean isMainNotSet = true;

        for (MultipartFile imageFile : jobPostImageFiles) {
            String imageUrl = imageStorage.upload(imageFile);
            JobPostImage newJobPostImage = JobPostImage.create(imageUrl, isMainNotSet, jobPostDetail);
            jobPostImages.add(newJobPostImage);
            isMainNotSet = false;
        }

        jobPostDetail.getJobPostImages().addAll(jobPostImages);
        jobPostDetailRepository.save(jobPostDetail);
    }

    @Transactional
    public void deleteByJobPost(JobPost jobPost) {
        List<JobPostImage> jobPostImages = jobPost.getJobPostDetail().getJobPostImages();
        if (!jobPostImages.isEmpty()) {
            imageStorage.deletePostImagesFromS3(jobPostImages);
        }
    }

    @Transactional
    public void deleteByJobPostImages(List<JobPostImage> jobPostImages, JobPostDetail jobPostDetail) {
        for (JobPostImage jobPostImage : jobPostImages) {
            imageStorage.deleteImageFromS3(jobPostImage.getJobPostImageUrl());
        }

        jobPostImageRepository.deleteAll(jobPostImages);
        jobPostDetail.getJobPostImages().clear();
    }

    @Transactional
    public void changeMainImage(Long currentImageId, Long newImageId) {
        JobPostImage currentMainImage = jobPostImageRepository.findById(currentImageId)
                .orElseThrow(ImageException.ImageNotFoundException::new);

        JobPostImage newMainImage = jobPostImageRepository.findById(newImageId)
                .orElseThrow(ImageException.ImageNotFoundException::new);

        currentMainImage.unsetMainImage();
        newMainImage.setMainImage();
    }

    public List<JobPostImage> getByJobPostDetailId(Long postDetailId) {
        return jobPostImageRepository.findByJobPostDetailId(postDetailId);
    }
}
