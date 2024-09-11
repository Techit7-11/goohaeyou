package com.ll.goohaeyou.chat.room.domain.policy;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.chat.room.domain.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomPolicy {

    // 채팅방 접근 권한 검증 (채팅방 참여 여부 및 퇴장 여부 확인)
    public void verifyRoomAccess(String username, Room room) {
        if (!username.equals(room.getUsername1()) && !username.equals(room.getUsername2())) {
            throw new AuthException.NotAuthorizedException();
        }

        if (username.equals(room.getUsername1()) && room.isUser1HasExit()) {
            throw new AuthException.NotAuthorizedException();
        } else if (username.equals(room.getUsername2()) && room.isUser2HasExit()) {
            throw new AuthException.NotAuthorizedException();
        }
    }
}
