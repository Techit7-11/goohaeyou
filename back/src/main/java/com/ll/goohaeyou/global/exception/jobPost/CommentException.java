package com.ll.goohaeyou.global.exception.jobPost;

import com.ll.goohaeyou.global.exception.ErrorCode;
import com.ll.goohaeyou.global.exception.GoohaeyouException;

import static com.ll.goohaeyou.global.exception.ErrorCode.COMMENT_NOT_EXISTS;

public class CommentException extends GoohaeyouException {

    public CommentException(ErrorCode e) {
        super(e);
    }

    public static class CommentNotExistsException extends CommentException {

        public CommentNotExistsException() {
            super(COMMENT_NOT_EXISTS);
        }
    }
}
