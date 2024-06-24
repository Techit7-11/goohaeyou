package com.ll.goohaeyou.domain.chat.message.entity.repository;

import com.ll.goohaeyou.domain.chat.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRoomIdAndCreatedAtAfter(Long roomId, LocalDateTime createdAt);
}
