package com.ll.goohaeyou.notification.domain;

import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.notification.domain.entity.Notification;
import com.ll.goohaeyou.notification.domain.repository.NotificationRepository;
import com.ll.goohaeyou.notification.exception.NotificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@DomainService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationManagementDomainService {
    private final NotificationRepository notificationRepository;

    @Transactional
    public void deleteAll(List<Notification> removeNotification) {
        notificationRepository.deleteAll(removeNotification);
    }

    public boolean existsByToMemberAndSeenIsFalse(Member toMember) {
        return notificationRepository.existsByToMemberAndSeenIsFalse(toMember);
    }

    public List<Notification> getByToMemberOrderByCreateAtDesc(Member member) {
        return notificationRepository.findByToMemberOrderByCreateAtDesc(member);
    }

    public List<Notification> getByToMember(Member toMember) {
        return notificationRepository.findByToMember(toMember);
    }

    public Notification getById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(NotificationException.NotificationNotExistsException::new);
    }
}
