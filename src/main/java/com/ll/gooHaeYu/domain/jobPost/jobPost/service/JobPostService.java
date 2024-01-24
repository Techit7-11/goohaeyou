package com.ll.gooHaeYu.domain.jobPost.jobPost.service;

import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.WriteJobPostRequestDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.repository.JobPostRepository;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import com.ll.gooHaeYu.global.exception.CustomException;
import com.ll.gooHaeYu.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostService {
    private final JobPostRepository jobPostRepository;
    private final MemberService memberService;

    public String writePost(String username, WriteJobPostRequestDto dto) {

        JobPost newPost = jobPostRepository.save(JobPost.builder()
                .member(memberService.getMember(username))
                .title(dto.getTitle())
                .body(dto.getBody())
                .build());

        return newPost.getId().toString();
    }

    public JobPost findById(Long id) {
        JobPost post = jobPostRepository.findById(id)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.POST_NOT_EXIST));
        return post;
    }

    public List<JobPost> findAll() {
        return jobPostRepository.findAll();
    }

    @Transactional
    public void modifyPost(String username, Long id, WriteJobPostRequestDto request) {
        JobPost post = findById(id);
        if (!canEditPost(username, post.getMember().getUsername())) throw new CustomException(ErrorCode.NOT_EDITABLE);

        post.update(request.getTitle(), request.getBody(), request.getQuestionItems());
    }

    @Transactional
    public void deletePost(String username, Long id) {
        JobPost post = findById(id);
        if (!canEditPost(username, post.getMember().getUsername())) throw new CustomException(ErrorCode.NOT_EDITABLE);

        jobPostRepository.deleteById(id);
    }

    public boolean canEditPost(String username, String author) {
        return username.equals(author) ? true : false;
    }
}
