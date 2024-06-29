package com.ll.goohaeyou.global.exception.member;

import com.ll.goohaeyou.global.exception.ErrorCode;
import com.ll.goohaeyou.global.exception.GoohaeyouException;

import static com.ll.goohaeyou.global.exception.ErrorCode.*;

public class EmailAuthException extends GoohaeyouException {

    public EmailAuthException(ErrorCode e) {
        super(e);
    }

    public static class EmailAlreadyAuthenticatedException extends EmailAuthException {

        public EmailAlreadyAuthenticatedException() {
            super(EMAIL_ALREADY_AUTHENTICATED);
        }
    }

    public static class InvalidAuthCodeException extends EmailAuthException {

        public InvalidAuthCodeException() {
            super(INVALID_AUTH_CODE);
        }
    }

    public static class InitiateEmailRequestException extends EmailAuthException {

        public InitiateEmailRequestException() {
            super(INITIATE_EMAIL_REQUEST);
        }
    }
}
