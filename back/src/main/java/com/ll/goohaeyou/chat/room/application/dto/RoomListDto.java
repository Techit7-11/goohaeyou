package com.ll.goohaeyou.chat.room.application.dto;

import com.ll.goohaeyou.chat.room.domain.entity.Room;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record RoomListDto(
        Long roomId,
        String username1,
        String username2,
        String lastChat,
        String lastChatDate
) {
    public static RoomListDto from(Room room) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        return new RoomListDto(
                room.getId(),
                room.getUsername1(),
                room.getUsername2(),
                room.getMessages().isEmpty() ? "메세지가 아직 없습니다." : room.getMessages().get(room.getMessages().size() - 1).getContent(),
                room.getMessages().isEmpty() ? "" : room.getMessages().get(room.getMessages().size() - 1).getCreatedAt().format(formatter)
        );
    }

    public static List<RoomListDto> convertToDtoList(List<Room> rooms) {
        return rooms.stream()
                .map(RoomListDto::from)
                .toList();
    }
}
