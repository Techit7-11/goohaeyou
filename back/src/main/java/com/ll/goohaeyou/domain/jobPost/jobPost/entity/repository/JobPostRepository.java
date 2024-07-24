package com.ll.goohaeyou.domain.jobPost.jobPost.entity.repository;

import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPost;
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
