package com.ll.goohaeyou.domain.notification.entity.repository;

import com.ll.goohaeyou.domain.member.member.entity.Member;
import com.ll.goohaeyou.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByToMemberOrderByCreateAtDesc(Member toMember);

    List<Notification> findByToMemberAndSeenIsTrue(Member toMember);

    List<Notification> findByToMember(Member toMember);

    Optional<Notification> findById(Long id);

    Boolean existsByToMemberAndSeenIsFalse(Member toMember);
}
