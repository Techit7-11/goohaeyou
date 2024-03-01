package com.ll.gooHaeYu.domain.chat.room.repository;

import com.ll.gooHaeYu.domain.chat.room.entity.Room;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r WHERE r.username1 = :username OR r.username2 = :username")
    List<Room> findByUsername1OrUsername2(@Param("username") String username);
}
