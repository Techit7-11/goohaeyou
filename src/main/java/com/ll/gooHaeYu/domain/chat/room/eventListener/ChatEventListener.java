package com.ll.gooHaeYu.domain.chat.room.eventListener;

import com.ll.gooHaeYu.domain.chat.room.service.RoomService;
import com.ll.gooHaeYu.domain.notification.service.NotificationService;
import com.ll.gooHaeYu.global.event.CreateChatRoomEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatEventListener {
    private final RoomService roomService;
    private final NotificationService notificationService;

    @EventListener
    public void createChatRoomEventListen(CreateChatRoomEvent event) {

        roomService.createRoom(event.getMemberId1(), event.getMemberId2());
//        notificationService.notifyAboutChatRoom(event);
    }


}
