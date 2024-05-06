package com.ll.gooHaeYu.domain.fileupload.service;

import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.ll.gooHaeYu.global.exception.ErrorCode.FILE_IS_EMPTY;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileImageService {
    private final MemberService memberService;
    private final S3ImageService s3ImageService;

    @Transactional
    public void uploadProfileImage(String username, MultipartFile profileImageFile) {
        Member member = memberService.getMember(username);

        if (profileImageFile == null ||  profileImageFile.isEmpty()) {
            throw new CustomException(FILE_IS_EMPTY);
        }

        if (member.getProfileImageUrl() != null) {
            s3ImageService.deleteImageFromS3(member.getProfileImageUrl());   // 기존의 프로필 이미지 파일을 S3에서 제거
        }

        String imageUrl = s3ImageService.upload(profileImageFile);
        member.setImageUrl(imageUrl);
    }

    public String getProfileImage(String username) {
        Member member = memberService.getMember(username);

        return member.getProfileImageUrl();
    }
}
