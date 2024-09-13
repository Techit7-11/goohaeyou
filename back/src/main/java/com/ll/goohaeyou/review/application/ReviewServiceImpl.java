package com.ll.goohaeyou.review.application;

import com.ll.goohaeyou.jobPost.jobPost.domain.service.JobPostDomainService;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.member.member.domain.service.MemberDomainService;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.review.application.dto.ApplicantReviewDto;
import com.ll.goohaeyou.review.domain.service.ReviewDomainService;
import com.ll.goohaeyou.review.domain.entity.Review;
import com.ll.goohaeyou.review.domain.mapper.ReviewMapper;
import com.ll.goohaeyou.review.domain.policy.ReviewPolicy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {
    private final ReviewMapper reviewMapper;
    private final ReviewPolicy reviewPolicy;
    private final MemberDomainService memberDomainService;
    private final JobPostDomainService jobPostDomainService;
    private final ReviewDomainService reviewDomainService;

    @Override
    @Transactional
    public ApplicantReviewDto saveReview(ApplicantReviewDto applicantReviewDto) {
        JobPost jobPost = jobPostDomainService.findById(applicantReviewDto.getJobPostingId());
        Member applicant = memberDomainService.getByUsername(getCurrentUsername());

        boolean exists = reviewDomainService.existsByJobPostingAndApplicant(jobPost,
                applicant);

        reviewPolicy.verifyReviewNotExists(exists);

        Review review = reviewDomainService.create(applicantReviewDto, jobPost, applicant);

        return reviewMapper.toApplicantReviewDto(review);
    }

    @Override
    public ApplicantReviewDto findReviewById(Long id) {
        Review review = reviewDomainService.getById(id);

        return reviewMapper.toApplicantReviewDto(review);
    }

    @Override
    public List<ApplicantReviewDto> findReviewsByCurrentUser() {
        Member currentUser = memberDomainService.getByUsername(getCurrentUsername());

        return reviewDomainService.findReviewsByApplicant(currentUser);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewDomainService.getById(id);

        reviewPolicy.verifyReviewAuthor(review.getApplicantId(), getCurrentUsername());

        reviewDomainService.deleteById(id);
    }

    public String getCurrentUsername() {
        return memberDomainService.getCurrentUsername();
    }
}
