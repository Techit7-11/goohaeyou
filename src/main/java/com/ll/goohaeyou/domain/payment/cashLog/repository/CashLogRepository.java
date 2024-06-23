package com.ll.goohaeyou.domain.payment.cashLog.repository;

import com.ll.goohaeyou.domain.payment.cashLog.entity.CashLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CashLogRepository extends JpaRepository<CashLog, Long> {
    Optional<CashLog> findByApplicationId(Long id);
}
