package com.ll.gooHaeYu.domain.jobPost.employ.service;

import com.ll.gooHaeYu.domain.application.application.dto.ApplicationDto;
import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.application.application.service.ApplicationService;
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
    private final ApplicationService applicationService;

    public List<ApplicationDto> getList(String username, Long postId) {
        JobPost post = jobPostService.findByIdAndValidate(postId);
        checkPermissions(username,post.getMember().getUsername());

        return ApplicationDto.toDtoList(post.getApplications());
    }

    @Transactional
    public void approve(String username, Long postId, List<Long> applicationIds) {
        JobPost post = jobPostService.findByIdAndValidate(postId);
        checkPermissions(username,post.getMember().getUsername());

        for (Application application : post.getApplications()) {
            if(applicationIds.contains(application.getId())) {
                application.approve();
            }else {
                application.reject();
            }
        }
    }

    public void checkPermissions (String username, String author){
        if (!username.equals(author)) throw new CustomException(ErrorCode.NOT_ABLE);
    }
}
