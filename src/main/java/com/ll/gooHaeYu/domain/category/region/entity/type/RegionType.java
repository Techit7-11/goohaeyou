package com.ll.gooHaeYu.domain.category.region.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RegionType {
    SEOUL("서울", 1),
    GYEONGGI("경기", 2),
    BUSAN("부산", 3),
    GYEONGNAM("경남", 4),
    INCHEON("인천", 5),
    GYEONGBUK("경북", 6),
    DAEGU("대구", 7),
    CHUNGNAM("충남", 8),
    JEONNAM("전남", 9),
    JEONBUK("전북", 10),
    CHUNGBUK("충북", 11),
    GANGWON("강원", 12),
    DAEJEON("대전", 13),
    GWANGJU("광주", 14),
    ULSAN("울산", 15),
    JEJU("제주", 16),
    SEJONG("세종", 17);

    private final String name;
    private final int code;
}
