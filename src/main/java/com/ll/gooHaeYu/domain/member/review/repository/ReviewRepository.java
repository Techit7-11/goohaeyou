package com.ll.gooHaeYu.domain.member.review.repository;

import com.ll.gooHaeYu.domain.member.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByApplicantId_Username(String username);
}
