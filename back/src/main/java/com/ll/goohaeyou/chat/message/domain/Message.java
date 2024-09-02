package com.ll.goohaeyou.chat.message.domain;

import com.ll.goohaeyou.chat.room.domain.Room;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@ToString(callSuper = true)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Room room;

    private String sender;

    private String content;

    private LocalDateTime createdAt;

    private Message(Room room, String sender, String content, LocalDateTime createdAt) {
        this.room = room;
        this.sender = sender;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static Message createMessage(Room room, String sender, String content, LocalDateTime createdAt) {
        return new Message(room, sender, content, createdAt);
    }

    public void update(String content) {
        if (content != null && !content.isBlank()) {
            this.content = content;
        }
    }
}
