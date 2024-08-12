package com.ll.goohaeyou.jobPost.jobPost.exception;

import com.ll.goohaeyou.global.exception.ErrorCode;
import com.ll.goohaeyou.global.exception.GoohaeyouException;

import static com.ll.goohaeyou.global.exception.ErrorCode.POST_NOT_EXIST;

public class JobPostException extends GoohaeyouException {

    public JobPostException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public static class PostNotExistsException extends JobPostException {

        public PostNotExistsException() {
            super(POST_NOT_EXIST);
        }
    }
}
