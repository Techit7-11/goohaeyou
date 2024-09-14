package com.ll.goohaeyou.jobApplication.exception;

import com.ll.goohaeyou.global.exception.ErrorCode;
import com.ll.goohaeyou.global.exception.GoohaeyouException;

import static com.ll.goohaeyou.global.exception.ErrorCode.*;

public class JobApplicationException extends GoohaeyouException {

    public JobApplicationException(ErrorCode e) {
        super(e);
    }

    public static class ClosedPostExceptionJob extends JobApplicationException {

        public ClosedPostExceptionJob() {
            super(CLOSED_POST);
        }
    }

    public static class DuplicateSubmissionExceptionJob extends JobApplicationException {

        public DuplicateSubmissionExceptionJob() {
            super(DUPLICATE_SUBMISSION);
        }
    }

    public static class NotEligibleForOwnJobExceptionJob extends JobApplicationException {

        public NotEligibleForOwnJobExceptionJob() {
            super(NOT_ELIGIBLE_FOR_OWN_JOB);
        }
    }
}
