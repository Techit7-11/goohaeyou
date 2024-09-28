package com.ll.goohaeyou.payment.payment.exception;

import com.ll.goohaeyou.global.exception.ErrorCode;
import com.ll.goohaeyou.global.exception.GoohaeyouException;

import static com.ll.goohaeyou.global.exception.ErrorCode.*;

public class PaymentException extends GoohaeyouException {

    public PaymentException(ErrorCode e) {
        super(e);
    }

    public static class PaymentAmountMismatchException extends PaymentException {

        public PaymentAmountMismatchException() {
            super(PAYMENT_AMOUNT_MISMATCH);
        }
    }

    public static class NoEnumConstantFoundException extends PaymentException {

        public NoEnumConstantFoundException() {
            super(NO_ENUM_CONSTANT_FOUND);
        }
    }

    public static class AlreadyCanceledException extends PaymentException {

        public AlreadyCanceledException() {
            super(ALREADY_CANCELED);
        }
    }

    public static class PaymentInfoNotFoundException extends PaymentException {

        public PaymentInfoNotFoundException() {
            super(PAYMENT_INFO_NOT_FOUND);
        }
    }

    public static class PaymentRequestConflictException extends PaymentException {

        public PaymentRequestConflictException() {
            super(PAYMENT_REQUEST_CONFLICT);
        }
    }

    public static class NoPendingPaymentException extends PaymentException {

        public NoPendingPaymentException() {
            super(NO_PENDING_PAYMENT);
        }
    }

    public static class ThrottleLimitExceededException extends PaymentException {

        public ThrottleLimitExceededException() {
            super((TOO_MANY_REQUESTS));
        }
    }
}
