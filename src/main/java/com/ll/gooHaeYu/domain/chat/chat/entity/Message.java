//package com.ll.gooHaeYu.domain.chat.chat.entity;
//
//import com.ll.gooHaeYu.domain.member.member.entity.Member;
//import jakarta.persistence.Entity;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//
//import static jakarta.persistence.GenerationType.IDENTITY;
//import static lombok.AccessLevel.PROTECTED;
//
//@Entity
//@Getter
//@Builder
//@NoArgsConstructor(access = PROTECTED)
//@AllArgsConstructor(access = PROTECTED)
//public class Message {
//
//    @Id
//    @GeneratedValue(strategy = IDENTITY)
//    private Long messageId;
//
//    @ManyToOne
//    @JoinColumn(name = "member_id", nullable = false)
//    private Member sender;
//
//    @ManyToOne
//    @JoinColumn(name = "room_id", nullable = false)
//    private Room room;
//
//    @Column(unique = false)
//    private String text;
//
//    @Column(unique = false)
//    private LocalDateTime sendTime;
//
//    public void setMember(Member sender) {
//        this.sender = sender;
//    }
//
//    public void room(Room room) {
//        this.room = room;
//    }
//
//}
