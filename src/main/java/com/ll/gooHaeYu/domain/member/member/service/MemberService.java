package com.ll.gooHaeYu.domain.member.member.service;

import com.ll.gooHaeYu.domain.member.member.dto.*;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.entity.type.Role;
import com.ll.gooHaeYu.domain.member.member.repository.MemberRepository;
import com.ll.gooHaeYu.global.exception.CustomException;
import com.ll.gooHaeYu.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static com.ll.gooHaeYu.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public JoinForm join(JoinForm form) {
        memberRepository.findByUsername(form.getUsername())
                .ifPresent(member -> {
                    throw new CustomException(DUPLICATE_USERNAME);
                });

        Role role = form.getUsername().equals("admin") ? Role.ADMIN : Role.USER;

        Member newMember = memberRepository.save(Member.builder()
                .username(form.getUsername())
                .password(bCryptPasswordEncoder.encode(form.getPassword()))
                .name(form.getName())
                .phoneNumber(form.getPhoneNumber())
                .email(form.getEmail())
                .gender(form.getGender())
                .location(form.getLocation())
                .birth(form.getBirth())
                .role(role)
                .build());
        return form;
    }

    public void login(LoginForm form) {
        Member member = getMember(form.getUsername());

        if (!bCryptPasswordEncoder.matches(form.getPassword(), member.getPassword())) {
            throw new CustomException(INVALID_PASSWORD);
        }
    }

    public Member getMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    public MemberDto findByUsername(String username) {
        Member member = getMember(username);

        return MemberDto.fromEntity(member);
    }

    @Transactional
    public void modifyMember(String username, MemberForm.Modify form) {
        Member member = getMember(username);

        String password = (form.getPassword() != null && !form.getPassword().isBlank())
                ? bCryptPasswordEncoder.encode(form.getPassword())
                : null;

        member.update(password, form.getGender(), form.getLocation(), form.getBirth());
    }

    public String findUsernameById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new CustomException(MEMBER_NOT_FOUND));

        return member.getUsername();
    }

    public Member findById(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    @Transactional
    public MemberDto updateSocialMemberProfile(String username, SocialProfileForm form) {
        Member member = getMember(username);

        if (member.getEmail() == null) {    // 소셜 회원은 본인인증을 할 필요가 없다
            member.authenticate();
        }

        member.oauthDetailUpdate(form);
        member.updateRole(Role.USER);

        return MemberDto.fromEntity(member);
    }

    private String saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomException(FILE_IS_EMPTY);
        }
        try {
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path storageDirectory = Paths.get(uploadDir);
            if (!Files.exists(storageDirectory)) {
                Files.createDirectories(storageDirectory);
            }
            Path destination = storageDirectory.resolve(Paths.get(filename));
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new CustomException(FILE_UPLOAD_ERROR);
        }
    }

    @Transactional
    public String updateMemberImage(Long memberId, MultipartFile file) {
        Member member = findById(memberId);
        String imageUrl = saveFile(file);
        member.setImageUrl(imageUrl);
        return imageUrl;
    }
}
