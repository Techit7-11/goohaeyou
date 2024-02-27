package com.ll.gooHaeYu.domain.chat.room.repository;

import com.ll.gooHaeYu.domain.chat.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}
