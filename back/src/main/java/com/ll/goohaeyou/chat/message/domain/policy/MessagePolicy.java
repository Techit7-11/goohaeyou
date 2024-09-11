package com.ll.goohaeyou.chat.message.domain.policy;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.chat.room.domain.Room;
import org.springframework.stereotype.Component;

@Component
public class MessagePolicy {

    public void verifyWritePermission(String username, Room room) {
        if (!username.equals(room.getUsername1()) && !username.equals(room.getUsername2())) {
            throw new AuthException.NotAuthorizedException();
        }
    }
}
