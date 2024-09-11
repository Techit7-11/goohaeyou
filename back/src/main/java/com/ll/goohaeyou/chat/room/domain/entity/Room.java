package com.ll.goohaeyou.chat.room.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.goohaeyou.chat.message.domain.entity.Message;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@ToString(callSuper = true)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username1;

    private String username2;

    private LocalDateTime user1Enter;

    private LocalDateTime user2Enter;

    private boolean user1HasExit = false;

    private boolean user2HasExit = false;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private List<Message> messages = new ArrayList<>();

    private Room(
            String username1,
            String username2
    ) {
        this.username1 = username1;
        this.username2 = username2;
        this.user1Enter = LocalDateTime.now();
        this.user2Enter = LocalDateTime.now();
    }

    public static Room create(
            String username1,
            String username2) {
        return new Room(
                username1,
                username2
        );
    }

    public void exit(String username) {
        if (username.equals(username1)) {
            this.user1HasExit = true;
            this.user1Enter = null;
        } else if (username.equals(username2)) {
            this.user2HasExit = true;
            this.user2Enter = null;
        }
    }

    public void recreate() {
        if (this.user1HasExit) {
            this.user1HasExit = false;
            this.user1Enter = LocalDateTime.now();
        } else if (this.user2HasExit){
            this.user2HasExit = false;
            this.user2Enter = LocalDateTime.now();
        }
    }
}
