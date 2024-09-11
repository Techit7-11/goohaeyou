package com.ll.goohaeyou.image.domain.policy;

import com.ll.goohaeyou.image.exception.ImageException;
import com.ll.goohaeyou.member.member.domain.Member;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ProfileImagePolicy {

    public void validateCanUploadImage(MultipartFile profileImageFile) {
        if (profileImageFile == null || profileImageFile.isEmpty()) {
            throw new ImageException.FileIsEmptyException();
        }
    }

    public void validateProfileImageExists(Member member) {
        if (member.getProfileImageUrl() == null) {
            throw new ImageException.ProfileImageNotFoundException();
        }
    }
}
