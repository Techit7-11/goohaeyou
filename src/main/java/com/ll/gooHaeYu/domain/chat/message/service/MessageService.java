package com.ll.gooHaeYu.domain.chat.message.service;

import com.ll.gooHaeYu.domain.chat.message.dto.MessageDto;
import com.ll.gooHaeYu.domain.chat.message.dto.MessageForm;
import com.ll.gooHaeYu.domain.chat.message.entity.Message;
import com.ll.gooHaeYu.domain.chat.message.repository.MessageRepository;
import com.ll.gooHaeYu.domain.chat.room.dto.RoomDto;
import com.ll.gooHaeYu.domain.chat.room.entity.Room;
import com.ll.gooHaeYu.domain.chat.room.service.RoomService;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import com.ll.gooHaeYu.global.security.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final MemberService memberService;
    private final RoomService roomService;


    @Transactional
    public Message write(String username, Long roomId, MessageForm.Register form) {
        Room room = roomService.findByIdAndValidate(roomId);

        Message message = Message.builder()
                .room(room)
                .sender(username)
                .content(form.getContent())
                .build();

        room.getMessages().add(message);



        return message;
    }

    public List<Message> findByRoomIdAndIdAfter(long roomId, long afterId) {
        return messageRepository.findByRoomIdAndIdAfter(roomId, afterId);
    }
}
