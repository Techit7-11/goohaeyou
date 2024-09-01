package com.ll.goohaeyou.jobPost.jobPost.application;

import com.ll.goohaeyou.jobPost.jobPost.application.dto.JobPostForm;
import com.ll.goohaeyou.jobPost.jobPost.domain.Essential;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.EssentialRepository;
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
