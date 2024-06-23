package com.ll.goohaeyou.domain.chat.room.eventListener;

import com.ll.goohaeyou.domain.chat.room.service.RoomService;
import com.ll.goohaeyou.domain.notification.service.NotificationService;
import com.ll.goohaeyou.global.event.notification.CreateChatRoomEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        notificationService.notifyAboutChatRoom(event);
    }


}
