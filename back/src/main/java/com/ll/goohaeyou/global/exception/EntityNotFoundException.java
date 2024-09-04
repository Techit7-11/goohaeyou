package com.ll.goohaeyou.global.exception;

import com.ll.goohaeyou.category.exception.CategoryException;
import com.ll.goohaeyou.chat.exception.ChatException;

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

    public static class ChatroomNotExistsException extends EntityNotFoundException {

        public ChatroomNotExistsException() {
            super(CHATROOM_NOT_EXISTS);
        }
    }

    public static class NotFoundCategoryException extends EntityNotFoundException {

        public NotFoundCategoryException() {
            super(NOT_FOUND_CATEGORY);
        }
    }
}
