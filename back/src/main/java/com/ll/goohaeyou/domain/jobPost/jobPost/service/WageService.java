package com.ll.goohaeyou.domain.jobPost.jobPost.service;

import com.ll.goohaeyou.domain.jobPost.jobPost.dto.JobPostForm;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.Wage;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.repository.WageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WageService {
    private final WageRepository wageRepository;

    @Transactional
    public void createAndSaveWage(JobPostDetail postDetail, JobPostForm.Register form) {
        Wage wage = Wage.builder()
                .cost(form.getCost())
                .workTime(form.getWorkTime())
                .workDays(form.getWorkDays())
                .payBasis(form.getPayBasis())
                .wagePaymentMethod(form.getWagePaymentMethod())
                .jobPostDetail(postDetail)
                .build();

        wageRepository.save(wage);
    }

    @Transactional
    public void updateWage(Wage wage, JobPostForm.Modify form) {
        wage.updateWageInfo(form.getCost(), form.getPayBasis(), form.getWorkTime(), form.getWorkDays());
        wageRepository.save(wage);
    }
}
