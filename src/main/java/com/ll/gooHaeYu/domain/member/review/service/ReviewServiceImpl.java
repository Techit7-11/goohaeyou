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
import jakarta.transaction.Transactional;
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
        JobPost jobPostId = jobPostRepository.findById(applicantReviewDto.getJobPostingId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_EXIST));

        Member applicantId = memberRepository.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Review review = reviewMapper.toEntity(applicantReviewDto);

        review.setJobPostingId(jobPostId);
        review.setApplicantId(applicantId);

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
    public List<ApplicantReviewDto> findReviewsByCurrentUser() {
        List<Review> reviews = reviewRepository.findByApplicantId_Username(getCurrentUsername());
        return reviews.stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_EXIST));

        if (!review.getApplicantId().getUsername().equals(getCurrentUsername())) {
            throw new CustomException(ErrorCode.NOT_ABLE);
        }

        reviewRepository.deleteById(id);
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
