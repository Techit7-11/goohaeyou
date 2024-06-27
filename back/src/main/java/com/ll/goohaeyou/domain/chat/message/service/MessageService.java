package com.ll.goohaeyou.domain.chat.message.service;

import com.ll.goohaeyou.domain.chat.message.dto.MessageDto;
import com.ll.goohaeyou.domain.chat.message.dto.MessageForm;
import com.ll.goohaeyou.domain.chat.message.entity.Message;
import com.ll.goohaeyou.domain.chat.message.entity.repository.MessageRepository;
import com.ll.goohaeyou.domain.chat.room.entity.Room;
import com.ll.goohaeyou.domain.chat.room.service.RoomService;
import com.ll.goohaeyou.global.exception.auth.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final RoomService roomService;
    private final MessageRepository messageRepository;

    @Transactional
    public Message write(String username, Long roomId, MessageForm.Register form) {
        Room room = roomService.findByIdAndValidate(roomId);

        if (!username.equals(room.getUsername1())&&!username.equals(room.getUsername2())) {
            throw new AuthException.NotAuthorizedException();
        }

        Message message = Message.builder()
                .room(room)
                .sender(username)
                .content(form.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        room.getMessages().add(message);

        return message;
    }

    public List<MessageDto> findByPostId(String username, Long roomId) {
        Room room = roomService.findByIdAndValidate(roomId);
        LocalDateTime enterDate = username.equals(room.getUsername1()) ? room.getUser1Enter() : room.getUser2Enter();

        List<Message> messages = messageRepository.findByRoomIdAndCreatedAtAfter(roomId, enterDate);
        Collections.reverse(messages);

        return MessageDto.convertToDtoList(messages);
    }
}
