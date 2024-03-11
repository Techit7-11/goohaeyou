package com.ll.gooHaeYu.domain.payment.cashLog.repository;

import com.ll.gooHaeYu.domain.payment.cashLog.entity.CashLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashLogRepository extends JpaRepository<CashLog, Long> {
}
