package com.ll.goohaeyou.chat.message.domain;

import com.ll.goohaeyou.chat.message.application.dto.MessageDto;
import com.ll.goohaeyou.chat.message.domain.entity.Message;
import com.ll.goohaeyou.chat.message.domain.repository.MessageRepository;
import com.ll.goohaeyou.chat.room.domain.entity.Room;
import com.ll.goohaeyou.global.standard.anotations.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@DomainService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageDomainService {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;

    @Transactional
    public Message create(Room room, String username, String content) {
        Message newMessage = Message.create(room, username, content);
        room.getMessages().add(newMessage);

        return messageRepository.save(newMessage);
    }

    @Transactional
    public void createInfoMessage(Room room, String username, String content) {
        Message newMessage = Message.create(room, username, content);
        room.getMessages().add(newMessage);
        messagingTemplate.convertAndSend("/queue/api/chat/" + room.getId() + "/newMessage", MessageDto.from(newMessage));
    }

    public List<Message> getMessagesAfterDate(Long roomId, LocalDateTime enterDate) {
        List<Message> messages = messageRepository.findByRoomIdAndCreatedAtAfter(roomId, enterDate);
        Collections.reverse(messages);  // 메시지 역순

        return messages;
    }
}
