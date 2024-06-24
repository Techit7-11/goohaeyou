package com.ll.goohaeyou.domain.jobPost.jobPost.entity.repository;

import com.ll.goohaeyou.domain.jobPost.jobPost.entity.Essential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EssentialRepository extends JpaRepository<Essential,Long> {
}
