package com.ll.goohaeyou.image.domain.policy;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.image.exception.ImageException;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostImage;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class JobPostImagePolicy {

    public void validateCanUploadImages(String username, JobPostDetail jobPostDetail, MultipartFile[] imageFiles) {
        verifyAuthor(username, jobPostDetail);
        validateImageFilesNotEmpty(imageFiles);
    }

    public void verifyCanDeleteJobPostImages(String username, JobPostDetail jobPostDetail, List<JobPostImage> postImages) {
        verifyAuthor(username, jobPostDetail);
        verifyPostImagesNotEmpty(postImages);
    }

    // 사용자 권한 검증
    public void verifyAuthor(String username, JobPostDetail jobPostDetail) {
        if (!jobPostDetail.getAuthor().equals(username)) {
            throw new AuthException.NotAuthorizedException();
        }
    }

    // 이미지 파일이 비어 있는지 검증
    private void validateImageFilesNotEmpty(MultipartFile[] imageFiles) {
        if (imageFiles.length == 0) {
            throw new ImageException.FileIsEmptyException();
        }
    }

    // 업로드된 이미지가 있는지 확인
    public void verifyPostImagesNotEmpty(List<JobPostImage> postImages) {
        if (postImages.isEmpty()) {
            throw new ImageException.PostImagesNotFoundException();
        }
    }
}
