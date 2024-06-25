package com.ll.goohaeyou.domain.chat.room.service;

import com.ll.goohaeyou.domain.chat.message.dto.MessageDto;
import com.ll.goohaeyou.domain.chat.message.entity.Message;
import com.ll.goohaeyou.domain.chat.room.dto.RoomDto;
import com.ll.goohaeyou.domain.chat.room.dto.RoomListDto;
import com.ll.goohaeyou.domain.chat.room.entity.Room;
import com.ll.goohaeyou.domain.chat.room.entity.repository.RoomRepository;
import com.ll.goohaeyou.domain.member.member.entity.Member;
import com.ll.goohaeyou.domain.member.member.service.MemberService;
import com.ll.goohaeyou.global.exception.GoohaeyouException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.ll.goohaeyou.global.exception.ErrorCode.CHATROOM_NOT_EXITS;
import static com.ll.goohaeyou.global.exception.ErrorCode.NOT_ABLE;

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

            String content = "매칭이 되었습니다.";

            createInfoMessage(room, "admin", content);

            room.recreate();

            return room.getId();
        } else {
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
            throw new GoohaeyouException(NOT_ABLE);
        }
        if (username.equals(room.getUsername1()) && room.isUser1HasExit()) {
            throw new GoohaeyouException(NOT_ABLE);
        } else if (username.equals(room.getUsername2()) && room.isUser2HasExit()) {
            throw new GoohaeyouException(NOT_ABLE);
        }
        return RoomDto.from(room);
    }

    public Room findByIdAndValidate(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new GoohaeyouException(CHATROOM_NOT_EXITS));
    }

    public List<RoomListDto> getRoomList(String username) {
        List<Room> rooms = roomRepository.findActiveRoomsByUsername(username);
        return RoomListDto.convertToDtoList(rooms);
    }

    @Transactional
    public void exitsRoom(String username, Long roomId) {
        Room room = findByIdAndValidate(roomId);

        if (room.isUser1HasExit() && room.isUser2HasExit()) {
            roomRepository.delete(room);
        }
        createInfoMessage(room, "admin","\""+username+"\" 님이 퇴장 하였습니다.");
        room.exit(username);
    }

    public Room findByUsername1AndUsername2(String username1, String username2) {
        return roomRepository.findByUsername1AndUsername2(username1, username2)
                .orElseGet(() -> roomRepository.findByUsername1AndUsername2(username2, username1)
                        .orElseThrow(() -> new GoohaeyouException(CHATROOM_NOT_EXITS)));
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

        messagingTemplate.convertAndSend("/queue/api/chat/"+room.getId()+ "/newMessage", MessageDto.from(message));
    }
}
