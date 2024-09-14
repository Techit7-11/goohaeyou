package com.ll.goohaeyou.chat.exception;

import com.ll.goohaeyou.global.exception.ErrorCode;
import com.ll.goohaeyou.global.exception.GoohaeyouException;

import static com.ll.goohaeyou.global.exception.ErrorCode.CHATROOM_NOT_EXISTS;
import static com.ll.goohaeyou.global.exception.ErrorCode.MESSAGE_NOT_EXISTS;

public class ChatException extends GoohaeyouException {

    public ChatException(ErrorCode e) {
        super(e);
    }

    public static class MessageNotExistsException extends ChatException {

        public MessageNotExistsException() {
            super(MESSAGE_NOT_EXISTS);
        }
    }
}
