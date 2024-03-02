package com.ll.gooHaeYu.domain.chat.room.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.gooHaeYu.domain.chat.message.entity.Message;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Setter
@Getter
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Builder
@ToString(callSuper = true)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username1;

    private String username2;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude

    @JsonIgnore
    private List<Message> messages = new ArrayList<>();

    public void update(String username) {
        if (username.equals(username1)) {
            this.username1 = null;
        }else if (username.equals(username2)) {
            this.username2 = null;
        }
    }
}
