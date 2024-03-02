package com.ll.gooHaeYu.domain.chat.message.entity;

import com.ll.gooHaeYu.domain.chat.room.entity.Room;
import com.ll.gooHaeYu.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Setter
@Getter
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Builder
@ToString(callSuper = true)
public class Message extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Room room;

    private String sender;

    private String content;

    public void update(String content) {
        if (content != null && !content.isBlank()) {
            this.content = content;
        }
    }
}
