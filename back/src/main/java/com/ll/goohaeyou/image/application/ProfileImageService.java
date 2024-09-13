package com.ll.goohaeyou.image.application;

import com.ll.goohaeyou.image.domain.service.ProfileImageDomainService;
import com.ll.goohaeyou.image.domain.policy.ProfileImagePolicy;
import com.ll.goohaeyou.jobPost.jobPost.domain.service.JobPostDomainService;
import com.ll.goohaeyou.member.member.domain.service.MemberDomainService;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileImageService {
    private final ProfileImagePolicy profileImagePolicy;
    private final MemberDomainService memberDomainService;
    private final JobPostDomainService jobPostDomainService;
    private final ProfileImageDomainService profileImageDomainService;

    @Transactional
    public void uploadProfileImage(String username, MultipartFile profileImageFile) {
        Member member = memberDomainService.getByUsername(username);

        profileImagePolicy.validateCanUploadImage(profileImageFile);

        if (member.getProfileImageUrl() != null) {
            profileImageDomainService.delete(member);
        }

        profileImageDomainService.upload(member, profileImageFile);
    }

    public String getMemberImageByUsername(String username) {
        Member member = memberDomainService.getByUsername(username);

        return member.getProfileImageUrl();
    }

    public String getMemberImageByPostId(Long postId) {
        Member member = jobPostDomainService.findById(postId).getMember();

        return member.getProfileImageUrl();
    }

    @Transactional
    public void deleteProfileImage(String username) {
        Member member = memberDomainService.getByUsername(username);

        profileImagePolicy.validateProfileImageExists(member);

        profileImageDomainService.delete(member);
    }
}
