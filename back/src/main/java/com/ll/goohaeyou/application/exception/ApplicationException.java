package com.ll.goohaeyou.application.exception;

import com.ll.goohaeyou.global.exception.ErrorCode;
import com.ll.goohaeyou.global.exception.GoohaeyouException;

import static com.ll.goohaeyou.global.exception.ErrorCode.*;

public class ApplicationException extends GoohaeyouException {

    public ApplicationException(ErrorCode e) {
        super(e);
    }

    public static class ClosedPostException extends ApplicationException {

        public ClosedPostException() {
            super(CLOSED_POST);
        }
    }

    public static class DuplicateSubmissionException extends ApplicationException {

        public DuplicateSubmissionException() {
            super(DUPLICATE_SUBMISSION);
        }
    }

    public static class NotEligibleForOwnJobException extends ApplicationException {

        public NotEligibleForOwnJobException() {
            super(NOT_ELIGIBLE_FOR_OWN_JOB);
        }
    }
}
