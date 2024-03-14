package com.ll.gooHaeYu.domain.payment.payment.entity.type;

import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.ll.gooHaeYu.global.exception.ErrorCode.NO_ENUM_CONSTANT_FOUND;

@Getter
@RequiredArgsConstructor
public enum PayStatus {
    REQUEST("결제요청"),
    CARD("카드"),
    EASY_PAY("간편결제");

    private final String description;    // 결제타입 설명

    public static PayStatus findPayTypeByDescription(String description) {
        return Arrays.stream(PayStatus.values())
                .filter(type -> type.getDescription().equals(description))
                .findFirst()
                .orElseThrow(() -> new CustomException(NO_ENUM_CONSTANT_FOUND));
    }

    public static PayStatus findByMethod(String method) {
        return switch (method) {
            case "카드" -> CARD;
            case "간편결제" -> EASY_PAY;
            default -> throw new CustomException(NO_ENUM_CONSTANT_FOUND);
        };
    }
}
