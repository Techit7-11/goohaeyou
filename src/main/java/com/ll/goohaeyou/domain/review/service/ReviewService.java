package com.ll.goohaeyou.domain.review.service;

import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPost;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.repository.JobPostRepository;
import com.ll.goohaeyou.domain.mapper.ReviewMapper;
import com.ll.goohaeyou.domain.member.member.entity.Member;
import com.ll.goohaeyou.domain.member.member.entity.repository.MemberRepository;
import com.ll.goohaeyou.domain.review.dto.ApplicantReviewDto;
import com.ll.goohaeyou.domain.review.entity.Review;
import com.ll.goohaeyou.domain.review.entity.repository.ReviewRepository;
import com.ll.goohaeyou.global.exception.auth.AuthException;
import com.ll.goohaeyou.global.exception.jobPost.JobPostException;
import com.ll.goohaeyou.global.exception.member.MemberException;
import com.ll.goohaeyou.global.exception.review.ReviewException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(JobPostException.PostNotExistsException::new);

        Member applicantId = memberRepository.findByUsername(getCurrentUsername())
                .orElseThrow(MemberException.MemberNotFoundException::new);

        // TODO 지원서 승인한 것만 후기 작성 가능하도록 OR 정산 이후 후기 작성 api 요청 하도록
        boolean exists = reviewRepository.existsByJobPostingId_IdAndApplicantId_Id(jobPostId.getId(),
                applicantId.getId());

        if (exists) {
            throw new ReviewException.ReviewAlreadyExistsException();
        }

        Review review = reviewMapper.toReviewEntity(applicantReviewDto);

        review.setJobPostingId(jobPostId);
        review.setApplicantId(applicantId);

        review = reviewRepository.save(review);

        return reviewMapper.toApplicantReviewDto(review);
    }

    public ApplicantReviewDto findReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(AuthException.NotAuthorizedException::new);

        return reviewMapper.toApplicantReviewDto(review);
    }

    public List<ApplicantReviewDto> findReviewsByCurrentUser() {
        List<Review> reviews = reviewRepository.findByApplicantId_Username(getCurrentUsername());

        return reviews.stream()
                .map(reviewMapper::toApplicantReviewDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(ReviewException.ReviewNotExistsException::new);

        if (!review.getApplicantId().getUsername().equals(getCurrentUsername())) {
            throw new AuthException.NotAuthorizedException();
        }

        reviewRepository.deleteById(id);
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName();
    }
}
