package com.ll.goohaeyou.jobPost.jobPost.application;

import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.JobPostDto;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.JobPostForm;
import com.ll.goohaeyou.jobPost.jobPost.domain.Essential;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.EssentialRepository;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EssentialService {
    private final EssentialRepository essentialRepository;
    private final JobPostDetailRepository jobPostDetailRepository;

    @Transactional
    public void create(JobPostDto jobPostDto, JobPostForm.Register form) {
        JobPostDetail jobPostDetail = jobPostDetailRepository.findById(jobPostDto.getId())
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);

        Essential newEssential = Essential.create(form.getMinAge(),
                form.getGender(), jobPostDetail);

        essentialRepository.save(newEssential);
    }

    @Transactional
    public void updateEssential(Essential essential, JobPostForm.Modify form) {
        essential.update(form.getMinAge(), form.getGender());
    }
}
