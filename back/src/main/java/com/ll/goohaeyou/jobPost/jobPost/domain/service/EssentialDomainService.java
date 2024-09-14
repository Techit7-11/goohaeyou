package com.ll.goohaeyou.jobPost.jobPost.domain.service;

import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.Essential;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.EssentialRepository;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostDetailRepository;
import com.ll.goohaeyou.member.member.domain.type.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@DomainService
@RequiredArgsConstructor
public class EssentialDomainService {
    private final EssentialRepository essentialRepository;
    private final JobPostDetailRepository jobPostDetailRepository;

    @Transactional
    public void create(Long jobPostId, int minAge, Gender gender) {
        JobPostDetail jobPostDetail = jobPostDetailRepository.findById(jobPostId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);

        Essential newEssential = Essential.create(minAge, gender, jobPostDetail);

        essentialRepository.save(newEssential);
    }
}
