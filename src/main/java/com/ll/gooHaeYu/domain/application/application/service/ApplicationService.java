package com.ll.gooHaeYu.domain.application.application.service;

import com.ll.gooHaeYu.domain.application.application.entity.Application;
import com.ll.gooHaeYu.domain.application.application.repository.ApplicationRepository;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationService {
    private final MemberService memberService;
    private final JobPostService jobPostService;
    private final ApplicationRepository applicationRepository;

    @Transactional
    public Long writeApplication(String username, Long id) {
        JobPost post = jobPostService.postAndApplication(id);

        Member member = memberService.getMember(username);

        Application newApplication = Application.builder()
                .member(member)
                .jobPost(post)
                .build();

        applicationRepository.save(newApplication);

        return newApplication.getId();
    }
}
