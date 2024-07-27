package com.ll.goohaeyou.domain.jobPost.jobPost.service;

import com.ll.goohaeyou.domain.jobPost.jobPost.dto.JobPostForm;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.Essential;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.repository.EssentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EssentialService {
    private final EssentialRepository essentialRepository;

    @Transactional
    public void createAndSaveEssential(JobPostDetail postDetail, JobPostForm.Register form) {
        Essential essential = Essential.builder()
                .minAge(form.getMinAge())
                .gender(form.getGender())
                .jobPostDetail(postDetail)
                .build();

        essentialRepository.save(essential);
    }

    @Transactional
    public void updateEssential(Essential essential, JobPostForm.Modify form) {
        essential.update(form.getMinAge(), form.getGender());
        essentialRepository.save(essential);
    }
}
