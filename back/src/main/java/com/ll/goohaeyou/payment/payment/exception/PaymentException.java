package com.ll.goohaeyou.payment.payment.exception;

import com.ll.goohaeyou.global.exception.ErrorCode;
import com.ll.goohaeyou.global.exception.GoohaeyouException;

import static com.ll.goohaeyou.global.exception.ErrorCode.*;

public class PaymentException extends GoohaeyouException {

    public PaymentException(ErrorCode e) {
        super(e);
    }

    public static class PaymentNotFoundException extends PaymentException {

        public PaymentNotFoundException() {
            super(PAYMENT_NOT_FOUND);
        }
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

    public static class InsufficientBalance extends PaymentException {

        public InsufficientBalance() {
            super(INSUFFICIENT_BALANCE);
        }
    }

    public static class PaymentRequestConflictException extends PaymentException {

        public PaymentRequestConflictException() {
            super(PAYMENT_REQUEST_CONFLICT);
        }
    }
}
