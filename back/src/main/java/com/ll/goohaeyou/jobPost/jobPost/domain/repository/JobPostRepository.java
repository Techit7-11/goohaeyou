package com.ll.goohaeyou.jobPost.jobPost.domain.repository;

import com.ll.goohaeyou.jobPost.jobPost.domain.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long>, JpaSpecificationExecutor<JobPost>, JobPostRepositoryCustom {

    List<JobPost> findByMemberId(Long id);

    List<JobPost> findByClosedFalseAndDeadlineBefore(LocalDate currentDate);
}
