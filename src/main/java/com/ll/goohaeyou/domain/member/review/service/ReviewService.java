package com.ll.goohaeyou.domain.member.review.service;

import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPost;
import com.ll.goohaeyou.domain.jobPost.jobPost.repository.JobPostRepository;
import com.ll.goohaeyou.domain.mapper.ReviewMapper;
import com.ll.goohaeyou.domain.member.member.entity.Member;
import com.ll.goohaeyou.domain.member.member.repository.MemberRepository;
import com.ll.goohaeyou.domain.member.review.dto.ApplicantReviewDto;
import com.ll.goohaeyou.domain.member.review.entity.Review;
import com.ll.goohaeyou.domain.member.review.repository.ReviewRepository;
import com.ll.goohaeyou.global.exception.CustomException;
import com.ll.goohaeyou.global.exception.ErrorCode;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewMapper reviewMapper;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final JobPostRepository jobPostRepository;

    @Transactional
    public ApplicantReviewDto saveReview(ApplicantReviewDto applicantReviewDto) {
        JobPost jobPostId = jobPostRepository.findById(applicantReviewDto.getJobPostingId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_EXIST));

        Member applicantId = memberRepository.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // TODO 지원서 승인한 것만 후기 작성 가능하도록 OR 정산 이후 후기 작성 api 요청 하도록
        boolean exists = reviewRepository.existsByJobPostingId_IdAndApplicantId_Id(jobPostId.getId(), applicantId.getId());
        if (exists) {
            throw new CustomException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Review review = reviewMapper.toEntity(applicantReviewDto);

        review.setJobPostingId(jobPostId);
        review.setApplicantId(applicantId);

        review = reviewRepository.save(review);
        return reviewMapper.toDto(review);
    }

    public ApplicantReviewDto findReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_ABLE));
        return reviewMapper.toDto(review);
    }

    public List<ApplicantReviewDto> findReviewsByCurrentUser() {
        List<Review> reviews = reviewRepository.findByApplicantId_Username(getCurrentUsername());
        return reviews.stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

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
