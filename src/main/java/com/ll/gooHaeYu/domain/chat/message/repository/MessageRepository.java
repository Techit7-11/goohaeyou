package com.ll.gooHaeYu.domain.chat.message.repository;

import com.ll.gooHaeYu.domain.chat.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
