package com.ll.gooHaeYu.domain.member.review.service;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.repository.JobPostRepository;
import com.ll.gooHaeYu.domain.mapper.ReviewMapper;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.repository.MemberRepository;
import com.ll.gooHaeYu.domain.member.review.dto.ApplicantReviewDto;
import com.ll.gooHaeYu.domain.member.review.entity.Review;
import com.ll.gooHaeYu.domain.member.review.repository.ReviewRepository;
import com.ll.gooHaeYu.global.exception.CustomException;
import com.ll.gooHaeYu.global.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final JobPostRepository jobPostRepository;

    @Override
    @Transactional
    public ApplicantReviewDto saveReview(ApplicantReviewDto applicantReviewDto) {
        JobPost jobPost = jobPostRepository.findById(applicantReviewDto.getJobPostingId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_EXIST));
        Member applicant = memberRepository.findById(applicantReviewDto.getApplicantId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Review review = reviewMapper.toEntity(applicantReviewDto);

        review.setJobPostingId(jobPost);
        review.setApplicantId(applicant);
        review.setRecruiterId(null);

        review = reviewRepository.save(review);
        return reviewMapper.toDto(review);
    }

    @Override
    public ApplicantReviewDto findReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_ABLE));
        return reviewMapper.toDto(review);
    }

    @Override
    public List<ApplicantReviewDto> findAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_ABLE));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!review.getApplicantId().getUsername().equals(currentUsername)) {
            throw new CustomException(ErrorCode.NOT_ABLE);
        }

        reviewRepository.deleteById(id);
    }
}
