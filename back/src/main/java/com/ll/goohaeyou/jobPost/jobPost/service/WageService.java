package com.ll.goohaeyou.jobPost.jobPost.service;

import com.ll.goohaeyou.jobPost.jobPost.dto.JobPostForm;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.Wage;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.WageRepository;
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
