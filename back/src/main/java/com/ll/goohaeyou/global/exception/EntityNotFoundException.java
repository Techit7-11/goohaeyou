package com.ll.goohaeyou.global.exception;

import static com.ll.goohaeyou.global.exception.ErrorCode.*;

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

    public static class JobApplicationNotExistsException extends EntityNotFoundException {

        public JobApplicationNotExistsException() {
            super(JOB_APPLICATION_NOT_EXISTS);
        }
    }
}
