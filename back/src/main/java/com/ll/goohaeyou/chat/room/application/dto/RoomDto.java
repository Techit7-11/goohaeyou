package com.ll.goohaeyou.chat.room.application.dto;

import com.ll.goohaeyou.chat.message.domain.entity.Message;
import com.ll.goohaeyou.chat.room.domain.entity.Room;

import java.util.List;

public record RoomDto(
        String username1,
        String username2,
        List<Message> messages,
        String otherUserImageUrl
) {
    public static RoomDto from(Room room, String otherUserImageUrl) {
        return new RoomDto(
                room.getUsername1(),
                room.getUsername2(),
                room.getMessages(),
                otherUserImageUrl
        );
    }
}
