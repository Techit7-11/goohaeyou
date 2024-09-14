package com.ll.goohaeyou.image.application;

import com.ll.goohaeyou.image.domain.service.JobPostImageDomainService;
import com.ll.goohaeyou.image.domain.policy.JobPostImagePolicy;
import com.ll.goohaeyou.jobPost.jobPost.domain.service.JobPostDetailDomainService;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostImageService {
    private final JobPostImagePolicy jobPostImagePolicy;
    private final JobPostDetailDomainService jobPostDetailDomainService;
    private final JobPostImageDomainService jobPostImageDomainService;

    @Transactional
    public void uploadJobPostImages(String username, long postDetailId, MultipartFile[] jobPostImageFiles) {
        JobPostDetail jobPostDetail = jobPostDetailDomainService.getById(postDetailId);

        jobPostImagePolicy.validateCanUploadImages(username, jobPostDetail, jobPostImageFiles);

        if (!jobPostDetail.getJobPostImages().isEmpty()) {
            deleteJobPostImages(username, postDetailId);
        }

        jobPostImageDomainService.addJobPostImages(jobPostDetail, jobPostImageFiles);
    }

    public List<String> getJobPostImages(Long postDetailId) {
        List<JobPostImage> postImages = jobPostImageDomainService.getByJobPostDetailId(postDetailId);

        jobPostImagePolicy.verifyPostImagesNotEmpty(postImages);

        return postImages.stream()
                .map(JobPostImage::getJobPostImageUrl)
                .toList();
    }

    @Transactional
    public void deleteJobPostImages(String username, long postDetailId) {
        JobPostDetail jobPostDetail = jobPostDetailDomainService.getById(postDetailId);
        List<JobPostImage> jobPostImages = jobPostDetail.getJobPostImages();

        jobPostImagePolicy.verifyCanDeleteJobPostImages(username, jobPostDetail, jobPostImages);

        jobPostImageDomainService.deleteByJobPostImages(jobPostImages, jobPostDetail);
    }

    @Transactional
    public void changeMainImage(String username, Long postId, Long currentImageId, Long newImageId) {
        JobPostDetail jobPostDetail = jobPostDetailDomainService.getById(postId);

        jobPostImagePolicy.verifyAuthor(username, jobPostDetail);

        jobPostImageDomainService.changeMainImage(currentImageId, newImageId);
    }
}
