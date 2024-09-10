package com.ll.goohaeyou.jobPost.jobPost.application;

import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.WriteJobPostRequest;
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
    public void create(Long jobPostId, WriteJobPostRequest request) {
        JobPostDetail jobPostDetail = jobPostDetailRepository.findById(jobPostId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);

        Wage newWage = Wage.create(request.cost(), request.workTime(), request.workDays(), request.payBasis(),
                request.wagePaymentMethod(), jobPostDetail);

        wageRepository.save(newWage);
    }
}
