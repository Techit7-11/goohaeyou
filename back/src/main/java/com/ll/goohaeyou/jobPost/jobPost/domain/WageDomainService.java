package com.ll.goohaeyou.jobPost.jobPost.domain;

import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.WriteJobPostRequest;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.Wage;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostDetailRepository;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.WageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@DomainService
@RequiredArgsConstructor
public class WageDomainService {
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
