package com.ll.goohaeyou.global.event.notification;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateChatRoomEvent extends ApplicationEvent {
    private final Long memberId1,memberId2;
    private final String postTitle;

    public CreateChatRoomEvent(Object source, Long memberId1, Long memberId2, String postTitle) {
        super(source);
        this.memberId1 = memberId1;
        this.memberId2 = memberId2;
        this.postTitle = postTitle;
    }
}
