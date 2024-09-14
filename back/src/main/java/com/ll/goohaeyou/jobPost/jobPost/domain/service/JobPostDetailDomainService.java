package com.ll.goohaeyou.jobPost.jobPost.domain.service;

import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.jobApplication.domain.entity.JobApplication;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.ModifyJobPostRequest;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.WriteJobPostRequest;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.Essential;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.Wage;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.EssentialRepository;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostDetailRepository;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.WageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@DomainService
@RequiredArgsConstructor
public class JobPostDetailDomainService {
    private final JobPostDetailRepository jobPostDetailRepository;
    private final WageRepository wageRepository;
    private final EssentialRepository essentialRepository;

    @Transactional
    public void create(JobPost newPost, String Author, WriteJobPostRequest request) {
        JobPostDetail newPostDetail = JobPostDetail.create(newPost, Author, request.body());

        jobPostDetailRepository.save(newPostDetail);
    }

    @Transactional
    public void update(JobPostDetail jobPostDetail, ModifyJobPostRequest request) {
        Wage existingWage = wageRepository.findByJobPostDetail(jobPostDetail);
        Essential existingEssential = essentialRepository.findByJobPostDetail(jobPostDetail);

        jobPostDetail.updatePostDetail(request.body());
        existingWage.updateWageInfo(request.cost(), request.payBasis(), request.workTime(), request.workDays());
        existingEssential.update(request.minAge(), request.gender());
    }

    @Transactional
    public void addJobApplication(JobPostDetail postDetail, JobApplication jobApplication) {
        postDetail.getJobApplications().add(jobApplication);
        postDetail.getJobPost().increaseApplicationsCount();
    }

    public JobPostDetail getById(Long id) {
        return jobPostDetailRepository.findById(id)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);
    }

    public List<JobPostDetail> getMyInterestedPosts(Long memberId) {
        return jobPostDetailRepository.findByInterestsMemberId(memberId);
    }
}
