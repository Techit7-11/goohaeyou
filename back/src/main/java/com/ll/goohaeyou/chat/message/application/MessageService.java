package com.ll.goohaeyou.chat.message.application;

import com.ll.goohaeyou.chat.message.application.dto.MessageDto;
import com.ll.goohaeyou.chat.message.application.dto.WriteMessageRequest;
import com.ll.goohaeyou.chat.message.domain.service.MessageDomainService;
import com.ll.goohaeyou.chat.message.domain.entity.Message;
import com.ll.goohaeyou.chat.message.domain.policy.MessagePolicy;
import com.ll.goohaeyou.chat.room.domain.service.RoomDomainService;
import com.ll.goohaeyou.chat.room.domain.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessagePolicy messagePolicy;
    private final MessageDomainService messageDomainService;
    private final RoomDomainService roomDomainService;

    @Transactional
    public Message write(String username, Long roomId, WriteMessageRequest request) {
        Room room = roomDomainService.getById(roomId);

        messagePolicy.verifyWritePermission(username, room);

        return messageDomainService.create(room, username, request.content());
    }

    public List<MessageDto> findByPostId(String username, Long roomId) {
        Room room = roomDomainService.getById(roomId);
        LocalDateTime enterDate = roomDomainService.getEnterDate(username, room);
        List<Message> messages = messageDomainService.getMessagesAfterDate(roomId, enterDate);

        return MessageDto.convertToDtoList(messages);
    }
}
