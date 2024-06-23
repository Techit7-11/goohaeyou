package com.ll.goohaeyou.domain.fileupload.service;

import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPostImage;
import com.ll.goohaeyou.domain.jobPost.jobPost.repository.JobPostDetailRepository;
import com.ll.goohaeyou.domain.jobPost.jobPost.repository.JobPostImageRepository;
import com.ll.goohaeyou.domain.jobPost.jobPost.service.JobPostService;
import com.ll.goohaeyou.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.ll.goohaeyou.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostImageService {
    private final JobPostService jobPostService;
    private final S3ImageService s3ImageService;
    private final JobPostImageRepository jobPostImageRepository;
    private final JobPostDetailRepository jobPostDetailRepository;

    @Transactional
    public void uploadJobPostImages(String username, long postDetailId, MultipartFile[] jobPostImageFiles) {
        JobPostDetail jobPostDetail = jobPostService.findByIdAndValidate(postDetailId).getJobPostDetail();

        if (!jobPostDetail.getAuthor().equals(username)) {
            throw new CustomException(NOT_ABLE);
        }

        if (jobPostImageFiles.length == 0) {
            throw new CustomException(FILE_IS_EMPTY);
        }

        if (!jobPostDetail.getJobPostImages().isEmpty()) {
            deleteJobPostImages(username, postDetailId);
        }

        List<JobPostImage> jobPostImages = new ArrayList<>();

        boolean isMainNotSet = true;
        for (MultipartFile imageFile : jobPostImageFiles) {
            String imageUrl = s3ImageService.upload(imageFile);

            JobPostImage jobPostImage = JobPostImage.builder()
                    .jobPostImageUrl(imageUrl)
                    .jobPostDetail(jobPostDetail)
                    .isMainImage(isMainNotSet)
                    .build();

            jobPostImages.add(jobPostImage);
            isMainNotSet = false;
        }

        jobPostDetail.getJobPostImages().addAll(jobPostImages);
        jobPostDetailRepository.save(jobPostDetail);
    }

    public List<String> getJobPostImages(Long postDetailId) {
        List<JobPostImage> postImages = jobPostImageRepository.findByJobPostDetailId(postDetailId);

        if (postImages.isEmpty()) {
            throw new CustomException(POST_IMAGES_NOT_FOUND);
        }

        return postImages.stream()
                .map(JobPostImage::getJobPostImageUrl)
                .toList();
    }

    @Transactional
    public void deleteJobPostImages(String username, long postDetailId) {
        JobPostDetail jobPostDetail = jobPostService.findByIdAndValidate(postDetailId).getJobPostDetail();
        List<JobPostImage> jobPostImages = jobPostDetail.getJobPostImages();

        if (!jobPostDetail.getAuthor().equals(username)) {
            throw new CustomException(NOT_ABLE);
        }

        if (jobPostImages.isEmpty()) {
            throw new CustomException(POST_IMAGES_NOT_FOUND);
        }

        for (JobPostImage jobPostImage : jobPostImages) {
            s3ImageService.deleteImageFromS3(jobPostImage.getJobPostImageUrl());
        }
        jobPostImageRepository.deleteAll(jobPostImages);
        jobPostDetail.getJobPostImages().clear();
    }

    @Transactional
    public void changeMainImage(String username, Long postId, Long currentImageId, Long newImageId) {
        String author = jobPostService.findByJobPostAndNameAndValidate(postId).getAuthor();

        if (!author.equals(username)) {
            throw new CustomException(NOT_ABLE);
        }

        JobPostImage currentMainImage = jobPostImageRepository.findById(currentImageId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_IMAGE));
        JobPostImage newMainImage = jobPostImageRepository.findById(newImageId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_IMAGE));

        currentMainImage.unsetMain();
        newMainImage.setMain();
    }
}
