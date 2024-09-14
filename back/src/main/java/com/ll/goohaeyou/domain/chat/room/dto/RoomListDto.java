package com.ll.goohaeyou.domain.chat.room.dto;

import com.ll.goohaeyou.domain.chat.room.entity.Room;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Builder
public class RoomListDto {
    private Long roomId;
    private String username1;
    private String username2;
    private String lastChat;
    private String lastChatDate;

    public static RoomListDto from(Room room) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        return RoomListDto.builder()
                .roomId(room.getId())
                .username1(room.getUsername1())
                .username2(room.getUsername2())
                .lastChat(room.getMessages().isEmpty() ? "메세지가 아직 없습니다." : room.getMessages().getLast().getContent())
                .lastChatDate(room.getMessages().isEmpty() ? "" : room.getMessages().getLast().getCreatedAt().format(formatter))
                .build();
    }

    public static List<RoomListDto> convertToDtoList(List<Room> rooms) {
        return rooms.stream()
                .map(RoomListDto::from)
                .toList();
    }
}
