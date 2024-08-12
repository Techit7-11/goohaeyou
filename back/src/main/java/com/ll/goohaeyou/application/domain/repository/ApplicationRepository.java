package com.ll.goohaeyou.application.domain.repository;

import com.ll.goohaeyou.application.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByMemberId(Long id);
}
