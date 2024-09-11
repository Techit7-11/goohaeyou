package com.ll.goohaeyou.chat.message.application;

import com.ll.goohaeyou.chat.message.application.dto.MessageDto;
import com.ll.goohaeyou.chat.message.application.dto.WriteMessageRequest;
import com.ll.goohaeyou.chat.message.domain.entity.Message;
import com.ll.goohaeyou.chat.message.domain.policy.MessagePolicy;
import com.ll.goohaeyou.chat.message.domain.repository.MessageRepository;
import com.ll.goohaeyou.chat.room.domain.entity.Room;
import com.ll.goohaeyou.chat.room.domain.repository.RoomRepository;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final MessagePolicy messagePolicy;

    @Transactional
    public Message write(String username, Long roomId, WriteMessageRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(EntityNotFoundException.ChatroomNotExistsException::new);

        messagePolicy.verifyWritePermission(username, room);

        Message newMessage = Message.create(room, username, request.content());

        room.getMessages().add(newMessage);

        return newMessage;
    }

    public List<MessageDto> findByPostId(String username, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(EntityNotFoundException.ChatroomNotExistsException::new);
        LocalDateTime enterDate = username.equals(room.getUsername1()) ? room.getUser1Enter() : room.getUser2Enter();

        List<Message> messages = messageRepository.findByRoomIdAndCreatedAtAfter(roomId, enterDate);
        Collections.reverse(messages);

        return MessageDto.convertToDtoList(messages);
    }
}
