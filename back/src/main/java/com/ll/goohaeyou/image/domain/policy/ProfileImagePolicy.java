package com.ll.goohaeyou.image.domain.policy;

import com.ll.goohaeyou.image.exception.ImageException;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Component
public class ProfileImagePolicy {

    public void validateCanUploadImage(MultipartFile profileImageFile) {
        if (Objects.isNull(profileImageFile) || profileImageFile.isEmpty()) {
            throw new ImageException.FileIsEmptyException();
        }
    }

    public void validateProfileImageExists(Member member) {
        if (Objects.isNull(member.getProfileImageUrl())) {
            throw new ImageException.ProfileImageNotFoundException();
        }
    }
}
