package com.ll.goohaeyou.notification.exception;

import com.ll.goohaeyou.global.exception.ErrorCode;
import com.ll.goohaeyou.global.exception.GoohaeyouException;

import static com.ll.goohaeyou.global.exception.ErrorCode.*;

public class NotificationException extends GoohaeyouException {

    public NotificationException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public static class NotificationNotExistsException extends NotificationException {

        public NotificationNotExistsException() {
            super(NOTIFICATION_NOT_EXISTS);
        }
    }
}
