package com.ll.goohaeyou.image.application;

import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostImage;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostDetailRepository;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostImageRepository;
import com.ll.goohaeyou.jobPost.jobPost.application.JobPostService;
import com.ll.goohaeyou.global.exception.auth.AuthException;
import com.ll.goohaeyou.image.exception.ImageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

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
            throw new AuthException.NotAuthorizedException();
        }

        if (jobPostImageFiles.length == 0) {
            throw new ImageException.FileIsEmptyException();
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
            throw new ImageException.PostImagesNotFoundException();
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
            throw new AuthException.NotAuthorizedException();
        }

        if (jobPostImages.isEmpty()) {
            throw new ImageException.PostImagesNotFoundException();
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
            throw new AuthException.NotAuthorizedException();
        }

        JobPostImage currentMainImage = jobPostImageRepository.findById(currentImageId)
                .orElseThrow(ImageException.ImageNotFoundException::new);

        JobPostImage newMainImage = jobPostImageRepository.findById(newImageId)
                .orElseThrow(ImageException.ImageNotFoundException::new);

        currentMainImage.unsetMain();
        newMainImage.setMain();
    }
}
