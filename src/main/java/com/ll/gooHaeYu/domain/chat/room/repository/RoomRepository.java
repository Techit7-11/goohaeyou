package com.ll.gooHaeYu.domain.chat.room.repository;

import com.ll.gooHaeYu.domain.chat.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r WHERE " +
            "(r.username1 = :username AND r.user1HasExit = false) " +
            "OR (r.username2 = :username AND r.user2HasExit = false)")
    List<Room> findActiveRoomsByUsername(@Param("username") String username);


    Optional<Room> findByUsername1AndUsername2(String username1, String username2);
}
