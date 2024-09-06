package com.ll.goohaeyou.payment.payment.domain.repository;

import com.ll.goohaeyou.payment.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(String orderId);
    Optional<Payment> findByPaymentKeyAndMemberUsername(String paymentKey, String Username);
    Optional<Payment> findByPaymentKey(String paymentKey);
    Optional<Payment> findByJobApplicationIdAndPaid(Long applicationId, boolean paid);

    List<Payment> findAllByMemberUsername(String username);   // '내 결제내역 전체 조회'에 사용
}
