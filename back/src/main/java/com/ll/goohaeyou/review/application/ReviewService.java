package com.ll.goohaeyou.review.application;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostRepository;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
import com.ll.goohaeyou.member.member.exception.MemberException;
import com.ll.goohaeyou.review.application.dto.ApplicantReviewDto;
import com.ll.goohaeyou.review.domain.entity.Review;
import com.ll.goohaeyou.review.domain.mapper.ReviewMapper;
import com.ll.goohaeyou.review.domain.policy.ReviewPolicy;
import com.ll.goohaeyou.review.domain.repository.ReviewRepository;
import com.ll.goohaeyou.review.exception.ReviewException;
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
    private final ReviewPolicy reviewPolicy;

    @Transactional
    public ApplicantReviewDto saveReview(ApplicantReviewDto applicantReviewDto) {
        JobPost jobPostId = jobPostRepository.findById(applicantReviewDto.getJobPostingId())
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);

        Member applicantId = memberRepository.findByUsername(getCurrentUsername())
                .orElseThrow(MemberException.MemberNotFoundException::new);

        // TODO 지원서 승인한 것만 후기 작성 가능하도록 OR 정산 이후 후기 작성 api 요청 하도록
        boolean exists = reviewRepository.existsByJobPostingId_IdAndApplicantId_Id(jobPostId.getId(),
                applicantId.getId());

        reviewPolicy.verifyReviewNotExists(exists);

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

        reviewPolicy.verifyReviewAuthor(review.getApplicantId(), getCurrentUsername());

        reviewRepository.deleteById(id);
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName();
    }
}
