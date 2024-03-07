package com.ll.gooHaeYu.domain.payment.payment.entity.type;

import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.ll.gooHaeYu.global.exception.ErrorCode.INVALID_PAYMENT_STATUS;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    READY("결제창_실행"),
    IN_PROGRESS("결제_진행_중"),
    DONE("승인_성공"),
    CANCELED("결제_취소"),
    ABORTED("승인_실패"),
    EXPIRED("결제시간_만료");

    private final String description;

    public static String findDescriptionByStatus(String status) {
        for (PaymentStatus paymentStatus : PaymentStatus.values()) {
            if (paymentStatus.toString().equals(status)) {
                return paymentStatus.getDescription();
            }
        }
        throw new CustomException(INVALID_PAYMENT_STATUS);
    }
}
