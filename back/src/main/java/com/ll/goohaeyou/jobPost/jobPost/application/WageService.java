package com.ll.goohaeyou.jobPost.jobPost.application;

import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.JobPostDto;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.JobPostForm;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.Wage;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostDetailRepository;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.WageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WageService {
    private final WageRepository wageRepository;
    private final JobPostDetailRepository jobPostDetailRepository;

    @Transactional
    public void create(JobPostDto jobPostDto, JobPostForm.Register form) {
        JobPostDetail jobPostDetail = jobPostDetailRepository.findById(jobPostDto.getId())
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);

        Wage newWage = Wage.create(form.getCost(), form.getWorkTime(), form.getWorkDays(), form.getPayBasis(),
                form.getWagePaymentMethod(), jobPostDetail);

        wageRepository.save(newWage);
    }

    @Transactional
    public void updateWage(Wage wage, JobPostForm.Modify form) {
        wage.updateWageInfo(form.getCost(), form.getPayBasis(), form.getWorkTime(), form.getWorkDays());
    }
}
