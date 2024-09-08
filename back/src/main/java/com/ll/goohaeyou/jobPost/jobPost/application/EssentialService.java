package com.ll.goohaeyou.jobPost.jobPost.application;

import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.jobPost.jobPost.domain.Essential;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.EssentialRepository;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostDetailRepository;
import com.ll.goohaeyou.member.member.domain.type.Gender;
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
    public void create(Long jobPostId, int minAge, Gender gender) {
        JobPostDetail jobPostDetail = jobPostDetailRepository.findById(jobPostId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);

        Essential newEssential = Essential.create(minAge, gender, jobPostDetail);

        essentialRepository.save(newEssential);
    }
}
