package com.ll.gooHaeYu.domain.jobPost.jobPost.repository;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long>, JobPostRepositoryCustom {
    List<JobPost> findByMemberId(Long id);

    List<JobPost> findByClosedFalseAndDeadlineBefore(LocalDate currentDate); //    LocalDate

//    List<JobPost> findByClosedFalseAndDeadlineBefore(LocalDateTime currentDateTime); //    LocalDateTime
}
