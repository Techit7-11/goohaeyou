package com.ll.goohaeyou.global.exception;

import static com.ll.goohaeyou.global.exception.ErrorCode.MEMBER_NOT_FOUND;

public class EntityNotFoundException extends GoohaeyouException {

    public EntityNotFoundException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public static class MemberNotFoundException extends EntityNotFoundException {

        public MemberNotFoundException() {
            super(MEMBER_NOT_FOUND);
        }
    }
}
