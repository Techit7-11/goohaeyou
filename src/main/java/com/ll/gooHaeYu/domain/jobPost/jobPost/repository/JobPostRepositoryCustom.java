package com.ll.gooHaeYu.domain.jobPost.jobPost.repository;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.standard.base.KwType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobPostRepositoryCustom {
    Page<JobPost> findBySort(Pageable pageable);

    Page<JobPost> findByKw(KwType kwType, String kw, Pageable pageable);
}
