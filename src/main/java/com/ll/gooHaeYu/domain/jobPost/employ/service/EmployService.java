package com.ll.gooHaeYu.domain.jobPost.employ.service;

import com.ll.gooHaeYu.domain.application.application.dto.ApplicationDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.global.exception.CustomException;
import com.ll.gooHaeYu.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployService {
    private final JobPostService jobPostService;

    public List<ApplicationDto> getList(String username, Long postId) {
        JobPost post = jobPostService.findByIdAndValidate(postId);
        if (!username.equals(post.getMember().getUsername())) throw new CustomException(ErrorCode.NOT_ABLE);

        return ApplicationDto.toDtoList(post.getApplications());
    }
}
