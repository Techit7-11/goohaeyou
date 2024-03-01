package com.ll.gooHaeYu.domain.chat.room.dto;

import com.ll.gooHaeYu.domain.chat.message.entity.Message;
import com.ll.gooHaeYu.domain.chat.room.entity.Room;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class RoomListDto {
    private Long roomId;
    private String username1;
    private String username2;
    private Message lastChat;

    public static RoomListDto fromEntity(Room room) {
        return RoomListDto.builder()
                .roomId(room.getId())
                .username1(room.getUsername1())
                .username2(room.getUsername2())
                .lastChat(room.getMessages().getLast())
                .build();
    }

    public static List<RoomListDto> toDtoList(List<Room> rooms) {
        return rooms.stream()
                .map(RoomListDto::fromEntity)
                .toList();
    }
}
