package com.ll.goohaeyou.image.application;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.image.exception.ImageException;
import com.ll.goohaeyou.jobApplication.domain.ImageStorage;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostImage;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostDetailRepository;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostImageRepository;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostRepository;
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
    private final JobPostRepository JobPostRepository;
    private final ImageStorage imageStorage;
    private final JobPostImageRepository jobPostImageRepository;
    private final JobPostDetailRepository jobPostDetailRepository;
    private final JobPostRepository jobPostRepository;

    @Transactional
    public void uploadJobPostImages(String username, long postDetailId, MultipartFile[] jobPostImageFiles) {
        JobPostDetail jobPostDetail = JobPostRepository.findById(postDetailId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new)
                .getJobPostDetail();

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
            String imageUrl = imageStorage.upload(imageFile);

            JobPostImage newJobPostImage = JobPostImage.create(imageUrl, isMainNotSet, jobPostDetail);

            jobPostImages.add(newJobPostImage);
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
        JobPostDetail jobPostDetail = jobPostRepository.findById(postDetailId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new)
                .getJobPostDetail();
        List<JobPostImage> jobPostImages = jobPostDetail.getJobPostImages();

        if (!jobPostDetail.getAuthor().equals(username)) {
            throw new AuthException.NotAuthorizedException();
        }

        if (jobPostImages.isEmpty()) {
            throw new ImageException.PostImagesNotFoundException();
        }

        for (JobPostImage jobPostImage : jobPostImages) {
            imageStorage.deleteImageFromS3(jobPostImage.getJobPostImageUrl());
        }

        jobPostImageRepository.deleteAll(jobPostImages);
        jobPostDetail.getJobPostImages().clear();
    }

    @Transactional
    public void changeMainImage(String username, Long postId, Long currentImageId, Long newImageId) {
        String author = jobPostDetailRepository.findById(postId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new)
                .getAuthor();

        if (!author.equals(username)) {
            throw new AuthException.NotAuthorizedException();
        }

        JobPostImage currentMainImage = jobPostImageRepository.findById(currentImageId)
                .orElseThrow(ImageException.ImageNotFoundException::new);

        JobPostImage newMainImage = jobPostImageRepository.findById(newImageId)
                .orElseThrow(ImageException.ImageNotFoundException::new);

        currentMainImage.unsetMainImage();
        newMainImage.setMainImage();
    }
}
