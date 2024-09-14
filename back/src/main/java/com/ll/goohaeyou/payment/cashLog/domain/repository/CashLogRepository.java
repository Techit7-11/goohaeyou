package com.ll.goohaeyou.payment.cashLog.domain.repository;

import com.ll.goohaeyou.payment.cashLog.domain.entity.CashLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CashLogRepository extends JpaRepository<CashLog, Long> {
    Optional<CashLog> findByJobApplicationId(Long id);
}
