package com.ll.goohaeyou.chat.room.application;

import com.ll.goohaeyou.chat.message.domain.service.MessageDomainService;
import com.ll.goohaeyou.chat.room.application.dto.RoomDto;
import com.ll.goohaeyou.chat.room.application.dto.RoomListDto;
import com.ll.goohaeyou.chat.room.domain.service.RoomDomainService;
import com.ll.goohaeyou.chat.room.domain.entity.Room;
import com.ll.goohaeyou.chat.room.domain.policy.RoomPolicy;
import com.ll.goohaeyou.member.member.domain.service.MemberDomainService;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RoomService {
    private final RoomPolicy roomPolicy;
    private final MemberDomainService memberDomainService;
    private final RoomDomainService roomDomainService;
    private final MessageDomainService messageDomainService;

    @Transactional
    public void createRoom(Long memberId1, Long memberId2) {
        Member member1 = memberDomainService.getById(memberId1);
        Member member2 = memberDomainService.getById(memberId2);

        if (roomDomainService.checkTheChatroom(member1.getUsername(), member2.getUsername())) {
            Room room = roomDomainService.getByUsername1AndUsername2(member1.getUsername(), member2.getUsername());
            messageDomainService.createInfoMessage(room, "admin", "매칭이 되었습니다.");
            room.recreate();
        } else {
            roomDomainService.createNewRoom(member1.getUsername(), member2.getUsername());
        }
    }

    public RoomDto findById(Long roomId, String username) {
        Room room = roomDomainService.getById(roomId);
        Member member1 = memberDomainService.getByUsername(room.getUsername1());

        roomPolicy.verifyRoomAccess(username, room);

        return RoomDto.from(room, member1.getProfileImageUrl());
    }

    public List<RoomListDto> getRoomList(String username) {
        List<Room> rooms = roomDomainService.getRoomListByUsername(username);

        return RoomListDto.convertToDtoList(rooms);
    }

    @Transactional
    public void exitsRoom(String username, Long roomId) {
        Room room = roomDomainService.getById(roomId);

        if (room.isUser1HasExit() && room.isUser2HasExit()) {
            roomDomainService.delete(room);
        }
        messageDomainService.createInfoMessage(room, "admin","\""+username+"\" 님이 퇴장 하였습니다.");
        room.exit(username);
    }
}
