package com.ll.goohaeyou.auth.exception;

import com.ll.goohaeyou.global.exception.ErrorCode;
import com.ll.goohaeyou.global.exception.GoohaeyouException;

import static com.ll.goohaeyou.global.exception.ErrorCode.*;

public class AuthException extends GoohaeyouException {

    public AuthException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public static class InvalidPasswordException extends AuthException {

        public InvalidPasswordException() {
            super(INVALID_PASSWORD);
        }
    }

    public static class NotAuthorizedException extends AuthException {

        public NotAuthorizedException() {
            super(NOT_AUTHORIZED);
        }
    }

    public static class InvalidLoginRequestException extends AuthException {

        public InvalidLoginRequestException() {
            super(INVALID_LOGIN_REQUEST);
        }
    }
}
