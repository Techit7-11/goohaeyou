package com.ll.gooHaeYu.domain.jobPost.jobPost.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskType {
    PERSONAL_ASSISTANCE("일상 도움", 1),
    CLEANING_AND_ORGANIZATION("정리 및 청소", 2),
    LOGISTICS_AND_DELIVERY("물류 및 배송", 3),
    TECHNICAL_TASKS("기술 작업", 4),
    STORE_MANAGEMENT("매장", 5),
    OFFICE_AND_EDUCATION("사무 및 교육", 6),
    EVENT_SUPPORT("행사", 7),
    OTHERS("기타", 8);

    private final String name;
    private final int code;
}
