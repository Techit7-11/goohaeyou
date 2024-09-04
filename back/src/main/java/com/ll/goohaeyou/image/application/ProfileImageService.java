package com.ll.goohaeyou.image.application;

import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.jobPost.jobPost.application.JobPostService;
import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.image.exception.ImageException;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileImageService {
    private final S3ImageService s3ImageService;
    private final JobPostService jobPostService;
    private final MemberRepository memberRepository;

    @Transactional
    public void uploadProfileImage(String username, MultipartFile profileImageFile) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);

        if (profileImageFile == null ||  profileImageFile.isEmpty()) {
            throw new ImageException.FileIsEmptyException();
        }

        if (member.getProfileImageUrl() != null) {
            deleteProfileImage(username);   // S3에서 이미지 제거, DB에서 이미지 url 제거
        }

        String imageUrl = s3ImageService.upload(profileImageFile);
        member.updateImageUrl(imageUrl);
    }

    public String getMemberImageByUsername(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);
        return member.getProfileImageUrl();
    }

    public String getMemberImageByPostId(Long postId) {
        Member member = jobPostService.findByIdAndValidate(postId).getMember();

        return member.getProfileImageUrl();
    }

    @Transactional
    public void deleteProfileImage(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);

        if (member.getProfileImageUrl() == null) {
            throw new ImageException.ProfileImageNotFoundException();
        }

        s3ImageService.deleteImageFromS3(member.getProfileImageUrl());
        member.updateImageUrl(null);
    }
}
