package com.ll.gooHaeYu.domain.jobPost.jobPost.repository;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobPostRepositoryCustom {
    Page<JobPost> findBySort(Pageable pageable);

    Page<JobPost> findByKw(List<String> kwTypes, String kw, String closed, String gender, int min_Age, Pageable pageable);
}
