package com.ll.gooHaeYu.domain.member.emailAuth.repository;

import com.ll.gooHaeYu.domain.member.emailAuth.entity.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {

    Optional<EmailAuth> findByUsername(String username);
}
