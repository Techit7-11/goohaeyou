package com.ll.goohaeyou.jobPost.jobPost.application;

import com.ll.goohaeyou.jobPost.jobPost.application.dto.JobPostForm;
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
        Wage newWage = Wage.create(form.getCost(), form.getWorkTime(), form.getWorkDays(), form.getPayBasis(),
                form.getWagePaymentMethod(), postDetail);

        wageRepository.save(newWage);
    }

    @Transactional
    public void updateWage(Wage wage, JobPostForm.Modify form) {
        wage.updateWageInfo(form.getCost(), form.getPayBasis(), form.getWorkTime(), form.getWorkDays());
    }
}
