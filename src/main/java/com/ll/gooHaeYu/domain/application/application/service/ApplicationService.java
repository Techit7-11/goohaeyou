package com.ll.gooHaeYu.domain.application.application.service;

import com.ll.gooHaeYu.domain.application.application.dto.ApplicationDto;
import com.ll.gooHaeYu.domain.application.application.dto.ApplicationForm;
import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.application.application.repository.ApplicationRepository;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
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
public class ApplicationService {
    private final MemberService memberService;
    private final JobPostService jobPostService;
    private final ApplicationRepository applicationRepository;

    @Transactional
    public Long writeApplication(String username, Long id, ApplicationForm.Register form) {
        JobPostDetail postDetail = jobPostService.findByJobPostAndNameAndValidate(id);

        Member member = memberService.getMember(username);

        Application newApplication = Application.builder()
                .member(member)
                .jobPostDetail(postDetail)
                .body(form.getBody())
                .approve(null)
                .build();
        postDetail.getApplications().add(newApplication);
        postDetail.getJobPost().increaseApplicationsCount();
        applicationRepository.save(newApplication);

        return newApplication.getId();
    }

    public ApplicationDto findById(Long id) {
        Application application = findByIdAndValidate(id);

        return ApplicationDto.fromEntity(application);
    }

    public Application findByIdAndValidate(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_EXIST));
    }

    @Transactional
    public void modifyApplication(String username, Long id, ApplicationForm.Modify form) {
        Application application = findByIdAndValidate(id);

        if (!canEditApplication(username, application.getMember().getUsername()))
            throw new CustomException(ErrorCode.NOT_ABLE);

        application.update(form.getBody());
    }

    public boolean canEditApplication(String username, String author) {
        return username.equals(author);
    }

    @Transactional
    public void deleteApplication(String username, Long id) {
        Application application = findByIdAndValidate(id);

        if (!canEditApplication(username, application.getMember().getUsername()))
            throw new CustomException(ErrorCode.NOT_ABLE);

        application.getJobPostDetail().getJobPost().decreaseApplicationsCount();
        applicationRepository.deleteById(id);
    }

    public List<ApplicationDto> findByUsername(String username) {

        Member member = memberService.getMember(username);

        return ApplicationDto.toDtoList(applicationRepository.findByMemberId(member.getId()));
    }
}
