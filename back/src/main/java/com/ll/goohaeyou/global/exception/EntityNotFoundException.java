package com.ll.goohaeyou.global.exception;

import static com.ll.goohaeyou.global.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.ll.goohaeyou.global.exception.ErrorCode.POST_NOT_EXIST;

public class EntityNotFoundException extends GoohaeyouException {

    public EntityNotFoundException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public static class MemberNotFoundException extends EntityNotFoundException {

        public MemberNotFoundException() {
            super(MEMBER_NOT_FOUND);
        }
    }

    public static class PostNotExistsException extends EntityNotFoundException {

        public PostNotExistsException() {
            super(POST_NOT_EXIST);
        }
    }
}
