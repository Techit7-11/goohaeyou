package com.ll.goohaeyou.image.domain.policy;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.image.exception.ImageException;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class JobPostImagePolicy {

    public void validateCanUploadImages(String username, JobPostDetail jobPostDetail, MultipartFile[] imageFiles) {
        verifyAuthor(username, jobPostDetail);
        validateImageFilesNotEmpty(imageFiles);
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
}
