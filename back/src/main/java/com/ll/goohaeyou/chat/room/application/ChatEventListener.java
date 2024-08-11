package com.ll.goohaeyou.chat.room.application;

import com.ll.goohaeyou.notification.application.NotificationService;
import com.ll.goohaeyou.global.event.notification.CreateChatRoomEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class ChatEventListener {
    private final RoomService roomService;
    private final NotificationService notificationService;

    @EventListener
    public void createChatRoomEventListen(CreateChatRoomEvent event) {

        roomService.createRoom(event.getMemberId1(), event.getMemberId2());
        notificationService.notifyAboutChatRoom(event);
    }
}
