package com.ll.goohaeyou.review.domain;

import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.review.application.dto.ApplicantReviewDto;
import com.ll.goohaeyou.review.domain.entity.Review;
import com.ll.goohaeyou.review.domain.mapper.ReviewMapper;
import com.ll.goohaeyou.review.domain.repository.ReviewRepository;
import com.ll.goohaeyou.review.exception.ReviewException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@DomainService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewDomainService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Transactional
    public Review create(ApplicantReviewDto applicantReviewDto, JobPost jobPost, Member applicant) {
        Review review = reviewMapper.toReviewEntity(applicantReviewDto);

        review.setJobPostingId(jobPost);
        review.setApplicantId(applicant);

        return reviewRepository.save(review);
    }

    public Review getById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(ReviewException.ReviewNotExistsException::new);
    }

    public List<ApplicantReviewDto> findReviewsByApplicant(Member applicant) {
        List<Review> reviews = reviewRepository.findByApplicantId_Username(applicant.getUsername());

        return reviews.stream()
                .map(reviewMapper::toApplicantReviewDto)
                .collect(Collectors.toList());
    }

    public boolean existsByJobPostingAndApplicant(JobPost jobPost, Member applicant) {
        return reviewRepository.existsByJobPostingId_IdAndApplicantId_Id(jobPost.getId(), applicant.getId());
    }

    @Transactional
    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }
}
