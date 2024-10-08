package com.ll.goohaeyou.jobPost.employ.exception;

import com.ll.goohaeyou.global.exception.ErrorCode;
import com.ll.goohaeyou.global.exception.GoohaeyouException;
import com.ll.goohaeyou.jobApplication.exception.JobApplicationException;

import static com.ll.goohaeyou.global.exception.ErrorCode.*;

public class EmployException extends GoohaeyouException {

    public EmployException(ErrorCode e) {
        super(e);
    }

    public static class NotPossibleToApproveItYetExceptionJob extends JobApplicationException {
        public NotPossibleToApproveItYetExceptionJob() {
            super(NOT_POSSIBLE_TO_APPROVE_IT_YET);
        }
    }

    public static class CompletionNotPossibleException extends EmployException {

        public CompletionNotPossibleException() {
            super(COMPLETION_NOT_POSSIBLE);
        }
    }

    public static class InvalidWagePaymentMethodException extends EmployException {

        public InvalidWagePaymentMethodException() {
            super(INVALID_WAGE_PAYMENT_METHOD);
        }
    }
}
