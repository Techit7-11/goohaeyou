package com.ll.gooHaeYu.global.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateChatRoomEvent extends ApplicationEvent {
    private final Long memberId1,memberId2;
    public CreateChatRoomEvent(Object source, Long memberId1, Long memberId2) {
        super(source);
        this.memberId1 = memberId1;
        this.memberId2 = memberId2;
    }
}
