package com.ll.goohaeyou.payment.payment.domain.type;

import com.ll.goohaeyou.payment.payment.exception.PaymentException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PayMethod {
    CARD("카드"),
    EASY_PAY("간편결제");

    private final String description;

    public static PayMethod findByMethod(String method) {
        return switch (method) {
            case "카드" -> CARD;
            case "간편결제" -> EASY_PAY;
            default -> throw new PaymentException.NoEnumConstantFoundException();
        };
    }
}
