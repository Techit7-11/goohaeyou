package com.ll.goohaeyou.chat.room.application;

import com.ll.goohaeyou.chat.message.application.dto.MessageDto;
import com.ll.goohaeyou.chat.message.domain.Message;
import com.ll.goohaeyou.chat.room.application.dto.RoomDto;
import com.ll.goohaeyou.chat.room.application.dto.RoomListDto;
import com.ll.goohaeyou.chat.room.domain.Room;
import com.ll.goohaeyou.chat.room.domain.policy.RoomPolicy;
import com.ll.goohaeyou.chat.room.domain.repository.RoomRepository;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RoomPolicy roomPolicy;

    @Transactional
    public void createRoom(Long memberId1, Long memberId2) {
        Member member1 = memberRepository.findById(memberId1)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);
        String member1Username = member1.getUsername();

        Member member2 = memberRepository.findById(memberId2)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);
        String member2Username = member2.getUsername();

        if (checkTheChatroom(member1Username, member2Username)) {
            Room room = findByUsername1AndUsername2(member1Username, member2Username);

            String content = "매칭이 되었습니다.";

            createInfoMessage(room, "admin", content);

            room.recreate();
        } else {
            Room newRoom = Room.create(member1Username, member2Username);

            roomRepository.save(newRoom);
        }
    }

    public RoomDto findById(Long roomId, String username) {
        Room room = findByIdAndValidate(roomId);

        roomPolicy.verifyRoomAccess(username, room);

        return RoomDto.from(room);
    }

    public Room findByIdAndValidate(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(EntityNotFoundException.ChatroomNotExistsException::new);
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
                        .orElseThrow(EntityNotFoundException.ChatroomNotExistsException::new));
    }

    public boolean checkTheChatroom(String username1, String username2) {
        Optional<Room> room = roomRepository.findByUsername1AndUsername2(username1, username2);
        room = room.isPresent() ? room : roomRepository.findByUsername1AndUsername2(username2,username1);
        return room.isPresent();
    }

    public void createInfoMessage(Room room, String username, String content) {
        Message newMessage = Message.create(room, username, content);
        room.getMessages().add(newMessage);

        messagingTemplate.convertAndSend("/queue/api/chat/"+room.getId()+ "/newMessage", MessageDto.from(newMessage));
    }
}
