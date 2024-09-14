package com.ll.goohaeyou.review.domain.repository;

import com.ll.goohaeyou.review.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByApplicantId_Username(String username);

    boolean existsByJobPostingId_IdAndApplicantId_Id(Long jobPostingId, Long applicantId);
}
