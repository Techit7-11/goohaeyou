package com.ll.gooHaeYu.domain.payment.payment.entity.type;

import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.ll.gooHaeYu.global.exception.ErrorCode.NO_ENUM_CONSTANT_FOUND;

@Getter
@RequiredArgsConstructor
public enum PayType {
    CARD_REQUEST("카드_결제요청"),
    CARD_CARD("카드_카드결제"),
    CARD_EASY_PAY("카드_간편결제");

    private final String description;    // 결제타입 설명

    public static PayType findByDescription(String description) {
        return Arrays.stream(PayType.values())
                .filter(type -> type.getDescription().equals(description))
                .findFirst()
                .orElseThrow(() -> new CustomException(NO_ENUM_CONSTANT_FOUND));
    }

    public static PayType findByMethod(String method) {
        return switch (method) {
            case "카드" -> CARD_CARD;
            case "간편결제" -> CARD_EASY_PAY;
            default -> throw new CustomException(NO_ENUM_CONSTANT_FOUND);
        };
    }
}
