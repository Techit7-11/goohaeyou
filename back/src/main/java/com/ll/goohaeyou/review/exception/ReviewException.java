package com.ll.goohaeyou.review.exception;

import com.ll.goohaeyou.global.exception.ErrorCode;
import com.ll.goohaeyou.global.exception.GoohaeyouException;

import static com.ll.goohaeyou.global.exception.ErrorCode.REVIEW_ALREADY_EXISTS;
import static com.ll.goohaeyou.global.exception.ErrorCode.REVIEW_NOT_EXISTS;

public class ReviewException extends GoohaeyouException {

    public ReviewException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public static class ReviewNotExistsException extends ReviewException {

        public ReviewNotExistsException() {
            super(REVIEW_NOT_EXISTS);
        }
    }

    public static class ReviewAlreadyExistsException extends ReviewException {

        public ReviewAlreadyExistsException() {
            super(REVIEW_ALREADY_EXISTS);
        }
    }
}
