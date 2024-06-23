package com.ll.goohaeyou.domain.chat.message.entity;

import com.ll.goohaeyou.domain.chat.room.entity.Room;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Setter
@Getter
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Builder
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

    public void update(String content) {
        if (content != null && !content.isBlank()) {
            this.content = content;
        }
    }
}
