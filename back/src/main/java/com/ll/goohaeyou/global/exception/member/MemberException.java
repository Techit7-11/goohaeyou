package com.ll.goohaeyou.global.exception.member;

import com.ll.goohaeyou.global.exception.ErrorCode;
import com.ll.goohaeyou.global.exception.GoohaeyouException;

import static com.ll.goohaeyou.global.exception.ErrorCode.DUPLICATE_USERNAME;
import static com.ll.goohaeyou.global.exception.ErrorCode.MEMBER_NOT_FOUND;

public class MemberException extends GoohaeyouException {

    public MemberException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public static class DuplicateUsernameException extends MemberException {

        public DuplicateUsernameException() {
            super(DUPLICATE_USERNAME);
        }
    }

    public static class MemberNotFoundException extends MemberException {

        public MemberNotFoundException() {
            super(MEMBER_NOT_FOUND);
        }
    }
}
