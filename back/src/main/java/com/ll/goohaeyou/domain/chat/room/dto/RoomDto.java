package com.ll.goohaeyou.domain.chat.room.dto;

import com.ll.goohaeyou.domain.chat.message.entity.Message;
import com.ll.goohaeyou.domain.chat.room.entity.Room;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoomDto {
    private String username1;
    private String username2;
    private List<Message> messages;

    public static RoomDto from(Room room) {
        return RoomDto.builder()
                .username1(room.getUsername1())
                .username2(room.getUsername2())
                .messages(room.getMessages())
                .build();
    }

    public static List<RoomDto> convertToDtoList(List<Room> rooms) {
        return rooms.stream()
                .map(RoomDto::from)
                .toList();
    }
}
