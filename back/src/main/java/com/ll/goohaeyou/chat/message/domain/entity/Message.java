package com.ll.goohaeyou.chat.message.domain.entity;

import com.ll.goohaeyou.chat.room.domain.entity.Room;
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

    private Message(
            Room room,
            String sender,
            String content
    ) {
        this.room = room;
        this.sender = sender;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public static Message create(
            Room room,
            String sender,
            String content
    ) {
        return new Message(
                room,
                sender,
                content
        );
    }

    public void update(String content) {
        if (content != null && !content.isBlank()) {
            this.content = content;
        }
    }
}
