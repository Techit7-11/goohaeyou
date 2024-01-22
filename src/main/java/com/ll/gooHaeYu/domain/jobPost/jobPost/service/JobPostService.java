package com.ll.gooHaeYu.domain.jobPost.jobPost.service;

import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.WriteJobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.repository.JobPostRepository;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import com.ll.gooHaeYu.global.exception.CustomException;
import com.ll.gooHaeYu.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostService {
    private final JobPostRepository jobPostRepository;
    private final MemberService memberService;

    @Value("${jwt.secret}")
    private String secretKey;
    private Long expiredMs = 1000 * 60 * 60L;

    public String writePost(String username, WriteJobPost dto) {

        // 글 작성 로직
        JobPost newJobPost = jobPostRepository.save(JobPost.builder()
                .member(memberService.getMember(username))
                .title(dto.getTitle())
                .body(dto.getBody())
                .build());

        return newJobPost.getId().toString();
    }

    public JobPost findById(Long id) {
        JobPost post = jobPostRepository.findById(id)
                .orElseThrow(()->
                        new CustomException(ErrorCode.POST_NOT_EXIST));
        return post;
    }

    @Transactional
    public String modifyPost(String username, Long id, WriteJobPost request) {
        JobPost post = findById(id);
        if (!canEditPost(username,post.getMember().getUsername())) throw  new CustomException(ErrorCode.NOT_EDITABLE);

        post.upData(request.getTitle(),request.getBody(),request.isClosed());

        return post.getId().toString();
    }

    @Transactional
    public String deletePost(String username, Long id) {
        JobPost post = findById(id);
        if (!canEditPost(username,post.getMember().getUsername())) throw  new CustomException(ErrorCode.NOT_EDITABLE);

        jobPostRepository.deleteById(id);
        return id.toString();
    }

    public boolean canEditPost(String username, String author){
        return username.equals(author)? true : false;
    }
}
