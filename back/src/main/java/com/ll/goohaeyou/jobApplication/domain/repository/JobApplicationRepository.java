package com.ll.goohaeyou.jobApplication.domain.repository;

import com.ll.goohaeyou.jobApplication.domain.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByMemberId(Long id);
}
