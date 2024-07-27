package com.ll.goohaeyou.domain.image.service;

import com.ll.goohaeyou.domain.jobPost.jobPost.service.JobPostService;
import com.ll.goohaeyou.domain.member.member.entity.Member;
import com.ll.goohaeyou.domain.member.member.service.MemberService;
import com.ll.goohaeyou.global.exception.image.ImageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileImageService {
    private final MemberService memberService;
    private final S3ImageService s3ImageService;
    private final JobPostService jobPostService;

    @Transactional
    public void uploadProfileImage(String username, MultipartFile profileImageFile) {
        Member member = memberService.getMember(username);

        if (profileImageFile == null ||  profileImageFile.isEmpty()) {
            throw new ImageException.FileIsEmptyException();
        }

        if (member.getProfileImageUrl() != null) {
            deleteProfileImage(username);   // S3에서 이미지 제거, DB에서 이미지 url 제거
        }

        String imageUrl = s3ImageService.upload(profileImageFile);
        member.setImageUrl(imageUrl);
    }

    public String getMemberImageByUsername(String username) {
        Member member = memberService.getMember(username);
        return member.getProfileImageUrl();
    }

    public String getMemberImageByPostId(Long postId) {
        Member member = jobPostService.findByIdAndValidate(postId).getMember();

        return member.getProfileImageUrl();
    }

    @Transactional
    public void deleteProfileImage(String username) {
        Member member = memberService.getMember(username);

        if (member.getProfileImageUrl() == null) {
            throw new ImageException.ProfileImageNotFoundException();
        }

        s3ImageService.deleteImageFromS3(member.getProfileImageUrl());
        member.setImageUrl(null);
    }
}
