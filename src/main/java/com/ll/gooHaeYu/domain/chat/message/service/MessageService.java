package com.ll.gooHaeYu.domain.chat.message.service;

import com.ll.gooHaeYu.domain.chat.message.dto.MessageDto;
import com.ll.gooHaeYu.domain.chat.message.dto.MessageForm;
import com.ll.gooHaeYu.domain.chat.message.entity.Message;
import com.ll.gooHaeYu.domain.chat.room.entity.Room;
import com.ll.gooHaeYu.domain.chat.room.service.RoomService;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.ll.gooHaeYu.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final RoomService roomService;

    @Transactional
    public Message write(String username, Long roomId, MessageForm.Register form) {
        Room room = roomService.findByIdAndValidate(roomId);

        if (!username.equals(room.getUsername1())&&!username.equals(room.getUsername2())) {
            throw new CustomException(NOT_ABLE);
        }

        Message message = Message.builder()
                .room(room)
                .sender(username)
                .content(form.getContent())
                .build();

        room.getMessages().add(message);

        return message;
    }

    public List<MessageDto> findByPostId(String username, Long roomId) {
        Room room = roomService.findByIdAndValidate(roomId);
        LocalDateTime enterDate = username.equals(room.getUsername1()) ? room.getUser1Enter() : room.getUser2Enter();

        List<Message> messages = room.getMessages().stream()
                .filter(message -> message.getCreatedAt().isAfter(enterDate))
                .collect(Collectors.toList());

        return MessageDto.toDtoList(messages);
    }
}
