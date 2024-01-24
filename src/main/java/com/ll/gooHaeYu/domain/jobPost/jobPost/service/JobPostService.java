package com.ll.gooHaeYu.domain.jobPost.jobPost.service;

import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostForm;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.QuestionItem;
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
    private final QuestionItemService questionItemService;

    @Transactional
    public Long writePost(String username, JobPostForm.Register form) {
        JobPost newPost = JobPost.builder()
                .member(memberService.getMember(username))
                .title(form.getTitle())
                .body(form.getBody())
                .build();

        jobPostRepository.save(newPost);

        List<QuestionItem> questionItems = form.getQuestionItemForms().stream()
                .map(formItem -> questionItemService.createQuestionItem(formItem, newPost))
                .toList();

        questionItemService.saveQuestionItems(questionItems);

        return newPost.getId();
    }

    public JobPostDto findById(Long id) {
        JobPost post = findByIdAndValidate(id);

        return JobPostDto.fromEntity(post);
    }

    public List<JobPostDto> findAll() {
        return JobPostDto.toDtoList(jobPostRepository.findAll());
    }

    @Transactional
    public void modifyPost(String username, Long id, JobPostForm.Modify form) {
        JobPost post = findByIdAndValidate(id);

        if (!canEditPost(username, post.getMember().getUsername()))
            throw new CustomException(ErrorCode.NOT_EDITABLE);

        post.update(form.getTitle(), form.getBody(), form.getClosed());
    }

    @Transactional
    public void deletePost(String username, Long id) {
        JobPost post = findByIdAndValidate(id);

        if (!canEditPost(username, post.getMember().getUsername()))
            throw new CustomException(ErrorCode.NOT_EDITABLE);

        jobPostRepository.deleteById(id);
    }

    public boolean canEditPost(String username, String author) {
        return username.equals(author);
    }

    private JobPost findByIdAndValidate(Long id) {
        return jobPostRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_EXIST));
    }
}
