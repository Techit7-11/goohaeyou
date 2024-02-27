//package com.ll.gooHaeYu.domain.chat.chat.entity;
//
//import com.ll.gooHaeYu.domain.member.member.entity.Member;
//import jakarta.persistence.Entity;
//import jakarta.persistence.*;
//import lombok.*;
//
//import static jakarta.persistence.GenerationType.IDENTITY;
//import static lombok.AccessLevel.PROTECTED;
//
//@Entity
//@Getter
//@Builder
//@NoArgsConstructor(access = PROTECTED)
//@AllArgsConstructor(access = PROTECTED)
//public class Room {
//
//    @Id
//    @GeneratedValue(strategy = IDENTITY)
//    private Long roomId;
//
//    @ManyToOne
//    @JoinColumn(name = "member_id", nullable = false)
//    private Member sender;
//
//    @ManyToOne
//    @JoinColumn(name = "member_id", nullable = false)
//    private Member receiver;
//
//    public void sender(Member sender) {
//        this.sender = sender;
//    }
//
//    public void receiver(Member receiver) {
//        this.receiver = receiver;
//    }
//
//}
