package com.ll.gooHaeYu.domain.chat.room.service;

import com.ll.gooHaeYu.domain.chat.message.dto.MessageDto;
import com.ll.gooHaeYu.domain.chat.message.entity.Message;
import com.ll.gooHaeYu.domain.chat.room.dto.RoomDto;
import com.ll.gooHaeYu.domain.chat.room.dto.RoomListDto;
import com.ll.gooHaeYu.domain.chat.room.entity.Room;
import com.ll.gooHaeYu.domain.chat.room.repository.RoomRepository;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.ll.gooHaeYu.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RoomService {
    private final RoomRepository roomRepository;
    private final MemberService memberService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Long createRoom(Long memberId1, Long memberId2) {

        Member member1 = memberService.findById(memberId1);
        String member1Username = member1.getUsername();
        Member member2 = memberService.findById(memberId2);
        String member2Username = member2.getUsername();

        if(checkTheChatroom(member1.getUsername(),member2.getUsername())) {
            Room room = findByUsername1AndUsername2(member1Username, member2Username);

            String content = room.isUser1HasExit() ? "\""+member1Username+"\" 님과 재매칭이 되었습니다." :
                    room.isUser2HasExit() ? "\""+member1Username+"\" 님과 재매칭이 되었습니다." : "재매칭이 되었습니다.";

            createInfoMessage(room, "admin", content);

            room.recreate();

            return room.getId();
        }else {
            Room newRoom = Room.builder()
                    .username1(member1.getUsername())
                    .username2(member2.getUsername())
                    .user1Enter(LocalDateTime.now())
                    .user2Enter(LocalDateTime.now())
                    .build();

            roomRepository.save(newRoom);

            return newRoom.getId();
        }
    }

    public RoomDto findById(Long roomId, String username) {
        Room room = findByIdAndValidate(roomId);
        if (!username.equals(room.getUsername1())&&!username.equals(room.getUsername2())) {
            throw new CustomException(NOT_ABLE);
        }
        if (username.equals(room.getUsername1()) && room.isUser1HasExit()) {
            throw new CustomException(NOT_ABLE);
        } else if (username.equals(room.getUsername2()) && room.isUser2HasExit()) {
            throw new CustomException(NOT_ABLE);
        }
        return RoomDto.fromEntity(room);
    }

    public Room findByIdAndValidate(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(CHATROOM_NOT_EXITS));
    }

    public List<RoomListDto> getRoomList(String username) {
        List<Room> rooms = roomRepository.findActiveRoomsByUsername(username);
        return RoomListDto.toDtoList(rooms);
    }

    @Transactional
    public void exitsRoom(String username, Long roomId) {
        Room room = findByIdAndValidate(roomId);

        if (room.isUser1HasExit() && room.isUser2HasExit()) {
            roomRepository.delete(room);
        }
        Message message = Message.builder()
                .room(room)
                .sender("admin")
                .content("\""+username+"\" 님이 퇴장 하였습니다.")
                .createdAt(LocalDateTime.now())
                .build();

        room.getMessages().add(message);
//        createInfoMessage(room, "admin","\""+username+"\" 님이 퇴장 하였습니다.");
        messagingTemplate.convertAndSend("/queue/api/chat/"+roomId+ "/newMessage", MessageDto.fromEntity(message));
        room.exit(username);
    }

    public Room findByUsername1AndUsername2(String username1, String username2) {
        return roomRepository.findByUsername1AndUsername2(username1, username2)
                .orElseGet(() -> roomRepository.findByUsername1AndUsername2(username2, username1)
                        .orElseThrow(() -> new CustomException(CHATROOM_NOT_EXITS)));
    }

    public boolean checkTheChatroom(String username1, String username2) {
        Optional<Room> room = roomRepository.findByUsername1AndUsername2(username1, username2);
        room = room.isPresent() ? room : roomRepository.findByUsername1AndUsername2(username2,username1);
        return room.isPresent();
    }

    public void createInfoMessage(Room room, String username, String content) {
        Message message = Message.builder()
                .room(room)
                .sender(username)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        room.getMessages().add(message);

        messagingTemplate.convertAndSend("/queue/api/chat/"+room.getId()+ "/newMessage", MessageDto.fromEntity(message));
    }
}
