package com.ll.goohaeyou.image.domain.service;

import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.jobApplication.domain.ImageStorage;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@DomainService
@RequiredArgsConstructor
public class ProfileImageDomainService {
    private final ImageStorage imageStorage;

    @Transactional
    public void upload(Member member, MultipartFile profileImageFile) {
        String imageUrl = imageStorage.upload(profileImageFile);
        member.updateImageUrl(imageUrl);
    }

    @Transactional
    public void delete(Member member) {   // S3에서 이미지 제거, DB에서 이미지 url 제거
        imageStorage.deleteImageFromS3(member.getProfileImageUrl());
        member.updateImageUrl(null);
    }
}
