package com.ll.goohaeyou.chat.room.domain.service;

import com.ll.goohaeyou.chat.room.domain.entity.Room;
import com.ll.goohaeyou.chat.room.domain.repository.RoomRepository;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.standard.anotations.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DomainService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomDomainService {
    private final RoomRepository roomRepository;

    @Transactional
    public void createNewRoom(String username1, String username2) {
        roomRepository.save(Room.create(username1, username2));
    }

    @Transactional
    public void delete(Room room) {
        roomRepository.delete(room);
    }

    public boolean checkTheChatroom(String username1, String username2) {
        Optional<Room> room = roomRepository.findByUsername1AndUsername2(username1, username2);
        room = room.isPresent() ? room : roomRepository.findByUsername1AndUsername2(username2, username1);

        return room.isPresent();
    }

    public Room getById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(EntityNotFoundException.ChatroomNotExistsException::new);
    }

    public LocalDateTime getEnterDate(String username, Room room) {
        return username.equals(room.getUsername1()) ? room.getUser1Enter() : room.getUser2Enter();
    }

    public Room getByUsername1AndUsername2(String username1, String username2) {
        return roomRepository.findByUsername1AndUsername2(username1, username2)
                .orElseGet(() -> roomRepository.findByUsername1AndUsername2(username2, username1)
                        .orElseThrow(EntityNotFoundException.ChatroomNotExistsException::new));
    }

    public List<Room> getRoomListByUsername(String username) {
        return roomRepository.findActiveRoomsByUsername(username);
    }
}
