package com.ll.goohaeyou.domain.application.application.repository;

import com.ll.goohaeyou.domain.application.application.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByMemberId(Long id);
}
