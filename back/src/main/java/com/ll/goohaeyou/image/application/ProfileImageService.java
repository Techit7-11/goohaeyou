package com.ll.goohaeyou.image.application;

import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.image.domain.policy.ProfileImagePolicy;
import com.ll.goohaeyou.jobApplication.domain.ImageStorage;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostRepository;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileImageService {
    private final ImageStorage imageStorage;
    private final JobPostRepository jobPostRepository;
    private final MemberRepository memberRepository;
    private final ProfileImagePolicy profileImagePolicy;

    @Transactional
    public void uploadProfileImage(String username, MultipartFile profileImageFile) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);

        profileImagePolicy.validateCanUploadImage(profileImageFile);

        if (member.getProfileImageUrl() != null) {
            deleteProfileImage(username);   // S3에서 이미지 제거, DB에서 이미지 url 제거
        }

        String imageUrl = imageStorage.upload(profileImageFile);
        member.updateImageUrl(imageUrl);
    }

    public String getMemberImageByUsername(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);
        return member.getProfileImageUrl();
    }

    public String getMemberImageByPostId(Long postId) {
        Member member = jobPostRepository.findById(postId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new)
                .getMember();

        return member.getProfileImageUrl();
    }

    @Transactional
    public void deleteProfileImage(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);

        profileImagePolicy.validateProfileImageExists(member);

        imageStorage.deleteImageFromS3(member.getProfileImageUrl());
        member.updateImageUrl(null);
    }
}
