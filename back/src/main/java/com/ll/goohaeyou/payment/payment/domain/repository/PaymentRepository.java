package com.ll.goohaeyou.payment.payment.domain.repository;

import com.ll.goohaeyou.payment.payment.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(String orderId);
    Optional<Payment> findByPaymentKeyAndMemberUsername(String paymentKey, String Username);
    Optional<Payment> findByPaymentKey(String paymentKey);
    Optional<Payment> findByJobApplicationIdAndPaid(Long jobApplicationId, boolean paid);
    boolean existsByOrderName(String orderName);
}
